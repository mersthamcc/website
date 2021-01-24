CREATE TABLE "user"
(
    id          SERIAL      NOT NULL,
    external_id VARCHAR(180) DEFAULT NULL,
    email       VARCHAR(64) NOT NULL,
    given_name  VARCHAR(180) DEFAULT NULL,
    family_name VARCHAR(180) DEFAULT NULL,
    roles       JSONB       NOT NULL,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX UNIQ_USER_EXTERNAL_ID ON "user" (external_id);
CREATE UNIQUE INDEX UNIQ_USER_EMAIL ON "user" (email);
