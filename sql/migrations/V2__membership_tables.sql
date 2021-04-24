CREATE TYPE attribute_type AS ENUM ('String', 'Number', 'Boolean', 'Date', 'Time', 'Timestamp', 'List', 'Option', 'Email');
CREATE TABLE attribute_definition
(
    id      SERIAL,
    key     VARCHAR(64)    NOT NULL,
    type    attribute_type NOT NULL,
    choices JSONB,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX IDX_ATTRIBUTE_DEFINITION_KEY ON attribute_definition (key);

CREATE TABLE member_form_section
(
    id  SERIAL,
    key VARCHAR(64) NOT NULL,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX IDX_MEMBER_FORM_SECTION_KEY ON member_form_section (key);

CREATE TABLE member_form_section_attribute
(
    member_form_section_id  BIGINT  NOT NULL,
    attribute_definition_id BIGINT  NOT NULL,
    sort_order              INT     NOT NULL,
    mandatory               BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (member_form_section_id, attribute_definition_id)
);
ALTER TABLE member_form_section_attribute
    ADD CONSTRAINT FK_MEMBER_FORM_SECTION_ATTRIBUTE_MEMBER_FORM_SECTION_ID FOREIGN KEY (member_form_section_id) REFERENCES member_form_section (id) NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE member_form_section_attribute
    ADD CONSTRAINT FK_MEMBER_FORM_SECTION_ATTRIBUTE_ATTRIBUTE_DEFINITION_ID FOREIGN KEY (attribute_definition_id) REFERENCES attribute_definition (id) NOT DEFERRABLE INITIALLY IMMEDIATE;

CREATE TABLE member_category
(
    id                SERIAL,
    key               VARCHAR(64) NOT NULL,
    registration_code VARCHAR(64) NULL,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX IDX_USER_KEY ON member_category (key);

CREATE TABLE member_category_form_section
(
    member_category_id     BIGINT NOT NULL,
    member_form_section_id BIGINT NOT NULL,
    sort_order             INT    NOT NULL,
    show_on_registration   BOOL   NOT NULL,
    PRIMARY KEY (member_category_id, member_form_section_id)
);
ALTER TABLE member_category_form_section
    ADD CONSTRAINT FK_MEMBER_CATEGORY_FORM_SECTION_MEMBER_CATEGORY_ID FOREIGN KEY (member_category_id) REFERENCES member_category (id) NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE member_category_form_section
    ADD CONSTRAINT FK_MEMBER_CATEGORY_FORM_SECTION_MEMBER_FORM_SECTION_ID FOREIGN KEY (member_form_section_id) REFERENCES member_form_section (id) NOT DEFERRABLE INITIALLY IMMEDIATE;

CREATE TABLE member
(
    id                SERIAL,
    type              VARCHAR(20) NOT NULL,
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


CREATE TYPE relationship_type AS ENUM ('owner', 'parent', 'emergency');
CREATE TABLE member_relationship
(
    member_id         BIGINT    NOT NULL,
    related_member_id BIGINT    NOT NULL,
    relationship_type relationship_type NOT NULL,
    PRIMARY KEY (member_id, related_member_id, relationship_type)
);

CREATE INDEX IDX_MEMBER_RELATIONSHIP_MEMBER_ID ON member_relationship (member_id);
ALTER TABLE member_relationship
    ADD CONSTRAINT FK_MEMBER_RELATIONSHIP_MEMBER_ID FOREIGN KEY (member_id) REFERENCES member (id) NOT DEFERRABLE INITIALLY IMMEDIATE;

CREATE INDEX IDX_RELATED_MEMBER_RELATIONSHIP_MEMBER_ID ON member_relationship (related_member_id);
ALTER TABLE member_relationship
    ADD CONSTRAINT FK_MEMBER_RELATIONSHIP_RELATED_MEMBER_ID FOREIGN KEY (related_member_id) REFERENCES member (id) NOT DEFERRABLE INITIALLY IMMEDIATE;
