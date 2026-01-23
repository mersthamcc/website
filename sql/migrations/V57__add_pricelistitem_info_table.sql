CREATE TABLE IF NOT EXISTS pricelist_item_info (
    pricelist_item_id BIGINT NOT NULL,
    key VARCHAR(48) NOT NULL,
    icon TEXT,
    description TEXT,
    PRIMARY KEY (
        pricelist_item_id,
        key
    )
);

ALTER TABLE
    pricelist_item_info ADD CONSTRAINT FK_PRICELIST_ITEM_INFO_PRICELIST_ITEM_ID
        FOREIGN KEY(pricelist_item_id) REFERENCES pricelist_item(id);
