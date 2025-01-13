CREATE TABLE coupon (
    id SERIAL NOT NULL,
    code VARCHAR(10) NOT NULL,
    description TEXT NOT NULL,
    owner_user_id VARCHAR(64) NOT NULL,
    "value" DECIMAL(10, 2) NOT NULL,
    redeem_date TIMESTAMP,
    applied_to_order_id BIGINT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE
    coupon ADD CONSTRAINT FK_MEMBER_COUPON_APPLIED_TO_ORDER_ID FOREIGN KEY(applied_to_order_id)
        REFERENCES "order"(id) NOT DEFERRABLE INITIALLY IMMEDIATE;