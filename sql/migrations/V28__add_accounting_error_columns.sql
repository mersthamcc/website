ALTER TABLE
    "order" ADD COLUMN accounting_error TEXT NULL;

ALTER TABLE
    payment ADD COLUMN accounting_error TEXT NULL;