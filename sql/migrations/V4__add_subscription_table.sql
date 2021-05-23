CREATE TABLE IF NOT EXISTS member_subscription
(
    member_id         BIGINT NOT NULL,
    year              INT NOT NULL,
    pricelist_item_id BIGINT NOT NULL,
    price             DECIMAL(10,2) NOT NULL,
    added_date        DATE NOT NULL DEFAULT CURRENT_DATE,

    PRIMARY KEY (member_id, year)
);

ALTER TABLE member_subscription
    ADD CONSTRAINT FK_MEMBER_SUBSCRIPTION_MEMBER_ID FOREIGN KEY (member_id) REFERENCES member (id) NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE member_subscription
    ADD CONSTRAINT FK_MEMBER_SUBSCRIPTION_PRICELIST_ITEM_ID FOREIGN KEY (pricelist_item_id) REFERENCES pricelist_item (id) NOT DEFERRABLE INITIALLY IMMEDIATE;
