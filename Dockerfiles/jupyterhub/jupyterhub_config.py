# Configuration file for JupyterHub
import os


def named_server_format_volume_name(template, spawner):
    ns = spawner.template_namespace()
    ns['servername'] =  'default' if (ns['servername'] in ('', None)) else ns['servername']
    return template.format(**ns)


c = get_config()

c.JupyterHub.active_server_limit = 10
c.JupyterHub.allow_named_servers = True
c.JupyterHub.upgrade_db = True
c.JupyterHub.reset_db = True
c.JupyterHub.shutdown_on_logout = True
c.JupyterHub.redirect_to_server = False

# Spawn single-user servers as Docker containers
c.JupyterHub.spawner_class = 'dockerspawner.DockerSpawner'
# c.DockerSpawner.container_image = os.environ['DOCKER_NOTEBOOK_IMAGE']

spawn_cmd = os.environ.get('DOCKER_SPAWN_CMD', "start-singleuser.sh")
c.DockerSpawner.extra_create_kwargs.update({'command': spawn_cmd})

network_name = os.environ['DOCKER_NETWORK_NAME']
c.DockerSpawner.use_internal_ip = True
c.DockerSpawner.network_name = network_name

c.DockerSpawner.extra_host_config = {
    'network_mode': network_name
}

c.DockerSpawner.image_whitelist = {
    'Preferred': os.environ['DOCKER_NOTEBOOK_IMAGE'],
    'SciPy': 'jupyter/scipy-notebook:latest'
}

notebook_dir = '/home/jovyan/work'
c.DockerSpawner.notebook_dir = notebook_dir
c.DockerSpawner.format_volume_name = named_server_format_volume_name
c.DockerSpawner.volumes = {
    '%s/{safe_username}/{servername}' % os.environ.get('JUPYTER_NOTEBOOK_HOST_DIRECTORY'): notebook_dir,
    '%s/{safe_username}/{servername}' % os.environ.get('JUPYTER_SETTINGS_HOST_DIRECTORY'): '/home/jovyan/.jupyter/lab',
    '%s/{safe_username}' % os.environ.get('JUPYTER_LOCALS_HOST_DIRECTORY'): '/home/jovyan/.local',
}
c.DockerSpawner.environ = {
    'JUPYTER_ENABLE_LAB': 'yes'
}

c.DockerSpawner.remove_containers = True
c.DockerSpawner.debug = True

c.JupyterHub.hub_ip = '0.0.0.0'
c.JupyterHub.hub_port = 8080
# c.JupyterHub.ip = '0.0.0.0'
# c.JupyterHub.port = 8000

c.JupyterHub.authenticator_class = 'oauthenticator.auth0.Auth0OAuthenticator'
c.Auth0OAuthenticator.oauth_callback_url = os.environ['OAUTH_CALLBACK_URL']
c.Auth0OAuthenticator.client_id = os.environ.get('AUTH0_CLIENT_ID')
c.Auth0OAuthenticator.client_secret = os.environ.get('AUTH0_CLIENT_SECRET')
c.Auth0OAuthenticator.scope = ['openid', 'email', 'profile']
c.Auth0OAuthenticator.auth0_subdomain = os.environ.get('AUTH0_SUBDOMAIN')

# Persist hub data on volume mounted inside container
#data_dir = os.environ.get('DATA_VOLUME_CONTAINER', '/data')
#c.JupyterHub.cookie_secret_file = os.path.join(data_dir, 'jupyterhub_cookie_secret')

c.JupyterHub.db_url = 'mysql+pymysql://root:{password}@{host}/{db}'.format(
    host=os.environ['MYSQL_HOST'],
    password=os.environ['MYSQL_ROOT_PASSWORD'],
    db=os.environ['MYSQL_DATABASE'],
)

# Whitelist users and admins
c.Authenticator.whitelist = whitelist = set()
c.Authenticator.admin_users = admin = set()
c.JupyterHub.admin_access = True
pwd = os.path.dirname(__file__)
with open(os.path.join(pwd, 'userlist')) as f:
    for line in f:
        if not line:
            continue
        parts = line.split()
        # in case of newline at the end of userlist file
        if len(parts) >= 1:
            name = parts[0]
            whitelist.add(name)
            if len(parts) > 1 and parts[1] == 'admin':
                admin.add(name)
