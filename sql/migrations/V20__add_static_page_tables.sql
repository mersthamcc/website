CREATE
    TABLE
        IF NOT EXISTS static_page(
            slug TEXT NOT NULL,
            content TEXT,
            title TEXT NOT NULL,
            sort_order INT DEFAULT 1000,
            PRIMARY KEY(slug)
        );
