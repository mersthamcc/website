CREATE TYPE attribute_type AS ENUM ('String', 'Number', 'Boolean', 'Date', 'Time', 'Timestamp', 'List');
CREATE TABLE attribute_definition
(
    id      SERIAL,
    key     VARCHAR(64)    NOT NULL,
    type    attribute_type NOT NULL,
    encrypt BOOLEAN DEFAULT FALSE,
    choices JSONB,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX IDX_ATTRIBUTE_DEFINITION_KEY ON attribute_definition (key);

CREATE TABLE member_category
(
    id                SERIAL,
    key               VARCHAR(64) NOT NULL,
    registration_code VARCHAR(64) NULL,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX IDX_USER_KEY ON member_category (key);

CREATE TABLE member
(
    id                SERIAL,
    given_name        VARCHAR(64) NOT NULL,
    family_name       VARCHAR(64) NOT NULL,
    gender            VARCHAR(1)  NOT NULL,
    registration_date TIMESTAMP   NOT NULL,
    owner_user_id     BIGINT      NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX IDX_MEMBER_OWNER_USER_ID ON member (owner_user_id);
ALTER TABLE member
    ADD CONSTRAINT FK_MEMBER_USER_ID FOREIGN KEY (owner_user_id) REFERENCES "user" (id) NOT DEFERRABLE INITIALLY IMMEDIATE;

CREATE TABLE member_attribute
(
    member_id    BIGINT    NOT NULL,
    attribute_id BIGINT    NOT NULL,
    created_date TIMESTAMP NOT NULL,
    updated_date TIMESTAMP NOT NULL,
    value        JSONB,
    PRIMARY KEY (member_id, attribute_id)
);

CREATE INDEX IDX_MEMBER_ATTRIBUTE_ATTRIBUTE_ID ON member_attribute (attribute_id);
ALTER TABLE member_attribute
    ADD CONSTRAINT FK_MEMBER_ATTRIBUTE_ATTRIBUTE_ID FOREIGN KEY (attribute_id) REFERENCES attribute_definition (id) ON UPDATE CASCADE ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE;

CREATE INDEX IDX_MEMBER_ATTRIBUTE_MEMBER_ID ON member_attribute (member_id);
ALTER TABLE member_attribute
    ADD CONSTRAINT FK_MEMBER_ATTRIBUTE_MEMBER_ID FOREIGN KEY (member_id) REFERENCES member (id) NOT DEFERRABLE INITIALLY IMMEDIATE;
