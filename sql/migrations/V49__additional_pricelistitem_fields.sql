ALTER TABLE pricelist_item
  ADD COLUMN students_only BOOLEAN NOT NULL DEFAULT FALSE,
  ADD COLUMN parent_discount BOOLEAN NOT NULL DEFAULT FALSE;
