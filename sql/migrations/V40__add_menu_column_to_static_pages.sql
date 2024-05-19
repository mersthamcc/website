ALTER TABLE static_page
    ADD COLUMN menu TEXT;

CREATE INDEX IDX_STATIC_PAGE_MENU ON static_page (menu);