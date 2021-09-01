CREATE
    TABLE
        IF NOT EXISTS payment(
            id SERIAL,
            order_id BIGINT NOT NULL,
            TYPE VARCHAR(32) NOT NULL,
            reference VARCHAR(256) NOT NULL,
            "date" DATE NOT NULL,
            amount DECIMAL(
                10,
                2
            ) NOT NULL,
            processing_fees DECIMAL(
                10,
                2
            ) NOT NULL,
            accounting_id VARCHAR(256),
            fees_accounting_id VARCHAR(256),
            collected BOOLEAN DEFAULT FALSE,
            reconciled BOOLEAN DEFAULT FALSE,
            PRIMARY KEY(id)
        );

ALTER TABLE
    payment ADD CONSTRAINT FK_PAYMENT_ORDER_ID FOREIGN KEY(order_id) REFERENCES "order"(id) NOT DEFERRABLE INITIALLY IMMEDIATE;