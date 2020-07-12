# Copyright (c) Jupyter Development Team.
# Distributed under the terms of the Modified BSD License.
FROM jupyterhub/jupyterhub-onbuild:1.0.0

# Install dockerspawner, oauth, postgres
RUN pip install --no-cache-dir oauthenticator dockerspawner jhub-swarmspawner sqlalchemy pymysql

COPY jupyterhub_config.py /srv/jupyterhub/
