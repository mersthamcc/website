CREATE
    TABLE
        event(
            id SERIAL NOT NULL,
            event_date TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
            title VARCHAR(255) NOT NULL,
            "path" TEXT NOT NULL,
            uuid TEXT NOT NULL,
            location VARCHAR(255),
            body TEXT NOT NULL,
            PRIMARY KEY(id)
        );

CREATE
    TABLE
        event_attribute(
            event_id INT NOT NULL,
            name VARCHAR(64) NOT NULL,
            value TEXT DEFAULT NULL,
            PRIMARY KEY(
                event_id,
                name
            )
        );

CREATE
    INDEX IDX_EVENT_ATTRIBUTE_EVENT_ID ON
    event_attribute(event_id);

ALTER TABLE
    event_attribute ADD CONSTRAINT FK_EVENT_ATTRIBUTE_EVENT_ID FOREIGN KEY(event_id) REFERENCES event(id) NOT DEFERRABLE INITIALLY IMMEDIATE;
