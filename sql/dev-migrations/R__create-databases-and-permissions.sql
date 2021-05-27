DROP
    DATABASE IF EXISTS grafana;

DROP
    DATABASE IF EXISTS keycloak;

DROP
    DATABASE IF EXISTS development;

DROP
    USER IF EXISTS grafana;

DROP
    USER IF EXISTS keycloak;

CREATE
    USER grafana PASSWORD '${apps_database_password}';

CREATE
    USER keycloak PASSWORD '${apps_database_password}';

CREATE
    DATABASE grafana OWNER grafana;

CREATE
    DATABASE keycloak OWNER keycloak;

CREATE
    DATABASE development OWNER postgres;
