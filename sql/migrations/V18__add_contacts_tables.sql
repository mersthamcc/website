CREATE
    TABLE
        contact_category(
            id SERIAL NOT NULL,
            title VARCHAR(255) NOT NULL,
            slug TEXT NOT NULL,
            PRIMARY KEY(id)
        );

CREATE
    UNIQUE INDEX IDX_CONTACT_CATEGORY_TITLE ON
    contact_category(title);

CREATE
    UNIQUE INDEX IDX_CONTACT_CATEGORY_SLUG ON
    contact_category(slug);

CREATE
    TABLE
        contact(
            id SERIAL NOT NULL,
            category_id INT NOT NULL,
            "position" VARCHAR(255) NOT NULL,
            slug TEXT NOT NULL,
            name JSONB NOT NULL,
            PRIMARY KEY(id)
        );

CREATE
    INDEX IDX_CONTACT_CATEGORY_ID ON
    contact(category_id);

ALTER TABLE
    contact ADD CONSTRAINT FK_CONTACT_CATEGORY_ID FOREIGN KEY(category_id) REFERENCES contact_category(id) NOT DEFERRABLE INITIALLY IMMEDIATE;

CREATE
    TABLE
        contact_method(
            contact_id INT NOT NULL,
            "method" VARCHAR(64) NOT NULL,
            value JSONB NOT NULL,
            PRIMARY KEY(
                contact_id,
                "method"
            )
        );

CREATE
    INDEX IDX_CONTACT_METHOD_CONTACT_ID ON
    contact_method(contact_id);

ALTER TABLE
    contact_method ADD CONSTRAINT FK_CONTACT_METHOD_CONTACT_ID FOREIGN KEY(contact_id) REFERENCES contact(id) NOT DEFERRABLE INITIALLY IMMEDIATE;
