CREATE TABLE pricelist_item
(
    id                    SERIAL,
    category_id           INTEGER NOT NULL,
    min_age               INTEGER NOT NULL,
    max_age               INTEGER,
    description           TEXT    NOT NULL,
    includes_match_fees   BOOLEAN,
    PRIMARY KEY (id)
);

ALTER TABLE pricelist_item
    ADD CONSTRAINT FK_PRICELIST_ITEM_MEMBER_CATEGORY_ID FOREIGN KEY (category_id) REFERENCES member_category (id) NOT DEFERRABLE INITIALLY IMMEDIATE;

CREATE INDEX IDX_PRICELIST_ITEM_CATEGORY_ID ON pricelist_item (category_id);

CREATE TABLE pricelist
(
    pricelist_item_id     INTEGER NOT NULL,
    date_from             DATE    NOT NULL,
    date_to               DATE    NOT NULL,
    price                 DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (pricelist_item_id, date_from, date_to)
);

ALTER TABLE pricelist
    ADD CONSTRAINT FK_PRICELIST_ITEM_MEMBER_CATEGORY_ID FOREIGN KEY (pricelist_item_id) REFERENCES pricelist_item (id) NOT DEFERRABLE INITIALLY IMMEDIATE;

CREATE INDEX IDX_PRICELIST_ITEM_DATE_FROM_DATE_TO ON pricelist (date_from, date_to);
