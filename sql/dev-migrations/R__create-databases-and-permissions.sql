DROP DATABASE IF EXISTS grafana;
DROP DATABASE IF EXISTS keycloak;
DROP DATABASE IF EXISTS development;

DROP USER IF EXISTS grafana;
DROP USER IF EXISTS keycloak;
DROP USER IF EXISTS ${content_user};

CREATE USER grafana PASSWORD '${apps_database_password}';
CREATE USER keycloak PASSWORD '${apps_database_password}';
CREATE USER ${content_user} PASSWORD '${content_user_password}';

CREATE DATABASE grafana OWNER grafana;
CREATE DATABASE keycloak OWNER keycloak;
CREATE DATABASE development OWNER postgres;

GRANT ALL ON DATABASE ${database_name} TO ${content_user};
GRANT USAGE ON SCHEMA public TO ${content_user} ;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO ${content_user};
GRANT USAGE, SELECT, UPDATE ON ALL SEQUENCES IN SCHEMA public TO ${content_user};