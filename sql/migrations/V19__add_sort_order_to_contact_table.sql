ALTER TABLE
    contact ADD COLUMN IF NOT EXISTS sort_order INT NOT NULL DEFAULT 1000;

ALTER TABLE
    contact_category ADD COLUMN IF NOT EXISTS sort_order INT NOT NULL DEFAULT 1000;