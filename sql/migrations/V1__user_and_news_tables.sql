CREATE SEQUENCE "user_id_seq" INCREMENT BY 1 MINVALUE 1 START 1;
CREATE TABLE news
(
    id           SERIAL                         NOT NULL,
    created_date TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
    publish_date TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
    title        VARCHAR(255)                   NOT NULL,
    body         TEXT                           NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE news_attribute
(
    id      SERIAL      NOT NULL,
    news_id INT         NOT NULL,
    name    VARCHAR(64) NOT NULL,
    value   VARCHAR(1024) DEFAULT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX IDX_E65FB03AB5A459A0 ON news_attribute (news_id);
CREATE TABLE news_comment
(
    id      SERIAL NOT NULL,
    news_id INT    NOT NULL,
    author  VARCHAR(255) DEFAULT NULL,
    body    TEXT   NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX IDX_C3904E8AB5A459A0 ON news_comment (news_id);
CREATE TABLE page
(
    id    SERIAL       NOT NULL,
    title VARCHAR(255) NOT NULL,
    slug  VARCHAR(128) NOT NULL,
    body  TEXT         NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE "user"
(
    id          INT         NOT NULL,
    external_id VARCHAR(180) DEFAULT NULL,
    email       VARCHAR(64) NOT NULL,
    given_name  VARCHAR(180) DEFAULT NULL,
    family_name VARCHAR(180) DEFAULT NULL,
    roles       JSON        NOT NULL,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX UNIQ_8D93D6499F75D7B0 ON "user" (external_id);
CREATE UNIQUE INDEX UNIQ_8D93D649E7927C74 ON "user" (email);
ALTER TABLE news_attribute
    ADD CONSTRAINT FK_E65FB03AB5A459A0 FOREIGN KEY (news_id) REFERENCES news (id) NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE news_comment
    ADD CONSTRAINT FK_C3904E8AB5A459A0 FOREIGN KEY (news_id) REFERENCES news (id) NOT DEFERRABLE INITIALLY IMMEDIATE;

