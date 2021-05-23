CREATE TABLE IF NOT EXISTS "order"
(
    id            SERIAL,
    "uuid"        VARCHAR(128),
    create_date   TIMESTAMP NOT NULL,
    accounting_id VARCHAR(128),
    owner_user_id VARCHAR(64) NOT NULL,

    PRIMARY KEY (id)
);

ALTER TABLE member_subscription
    ADD COLUMN IF NOT EXISTS order_id BIGINT NULL;

ALTER TABLE member_subscription
    ADD CONSTRAINT FK_MEMBER_SUBSCRIPTION_ORDER_ID FOREIGN KEY (order_id) REFERENCES "order" (id) NOT DEFERRABLE INITIALLY IMMEDIATE;