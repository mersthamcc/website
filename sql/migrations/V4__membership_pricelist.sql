CREATE TABLE pricelist
(
    id                    SERIAL,
    category_id           INTEGER NOT NULL,
    date_from             DATE    NOT NULL,
    date_to               DATE    NOT NULL,
    min_age               INTEGER NOT NULL,
    max_age               INTEGER,
    description           TEXT    NOT NULL,
    includes_match_fees   BOOLEAN,
    price                 DECIMAL(10, 2) NOT NULL,
    additional_unit_price DECIMAL(10, 2),
    PRIMARY KEY (id)
);

ALTER TABLE pricelist
    ADD CONSTRAINT FK_PRICELIST_MEMBER_CATEGORY_ID FOREIGN KEY (category_id) REFERENCES member_category (id) NOT DEFERRABLE INITIALLY IMMEDIATE;

CREATE INDEX IDX_PRICELIST_DATE_FROM_DATE_TO ON pricelist (date_from, date_to);
CREATE INDEX IDX_PRICELIST_CATEGORY_ID ON pricelist (category_id);
CREATE UNIQUE INDEX IDX_PRICELIST_DATE_FROM_DATE_TO_CATEGORY_ID ON pricelist (date_from, date_to, category_id);
