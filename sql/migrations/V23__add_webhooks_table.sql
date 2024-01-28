CREATE
    TABLE
        webhook_received(
            id SERIAL NOT NULL,
            received TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
            TYPE VARCHAR(64) NOT NULL,
            reference VARCHAR(128) NULL,
            headers JSONB,
            body JSONB,
            processed BOOLEAN,
            PRIMARY KEY(id)
        );
