CREATE TYPE attribute_type AS ENUM ('String', 'Number', 'Boolean', 'Date', 'Time', 'Timestamp');
CREATE TABLE attribute_definition
(
    id      SERIAL,
    key     VARCHAR(64) NOT NULL,
    type    attribute_type  NOT NULL,
    encrypt BOOLEAN DEFAULT FALSE,
    choices JSONB,
    PRIMARY KEY (id)
);

CREATE TABLE member_category
(
    id SERIAL,
    key VARCHAR(64) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE member
(
    id                SERIAL,
    given_name        VARCHAR(64) NOT NULL,
    family_name       VARCHAR(64) NOT NULL,
    gender            VARCHAR(1) NOT NULL,
    dob               DATE,
    registration_date TIMESTAMP NOT NULL,
    category_id       BIGINT NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX IDX_MEMBER_CATEGORY_ID ON member (category_id);
ALTER TABLE member
    ADD CONSTRAINT FK_MEMBER_MEMBER_CATEGORY_ID FOREIGN KEY (category_id) REFERENCES member_category (id) NOT DEFERRABLE INITIALLY IMMEDIATE;

CREATE TABLE member_attribute
(
    id              SERIAL,
    created_date    TIMESTAMP NOT NULL,
    attribute_id    BIGINT NOT NULL,
    member_id       BIGINT NOT NULL,
    value           JSONB,
    PRIMARY KEY (id)
);
CREATE INDEX IDX_MEMBER_ATTRIBUTE_ATTRIBUTE_ID ON member_attribute (attribute_id);
ALTER TABLE member_attribute
    ADD CONSTRAINT FK_MEMBER_ATTRIBUTE_ATTRIBUTE_ID FOREIGN KEY (attribute_id) REFERENCES attribute_definition (id) NOT DEFERRABLE INITIALLY IMMEDIATE;
CREATE INDEX IDX_MEMBER_ATTRIBUTE_MEMBER_ID ON member_attribute (member_id);
ALTER TABLE member_attribute
    ADD CONSTRAINT FK_MEMBER_ATTRIBUTE_MEMBER_ID FOREIGN KEY (member_id) REFERENCES member (id) NOT DEFERRABLE INITIALLY IMMEDIATE;
