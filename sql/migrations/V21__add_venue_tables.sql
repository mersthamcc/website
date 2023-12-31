CREATE
    TABLE
        venue(
            slug VARCHAR(64) NOT NULL,
            name VARCHAR(100) NOT NULL,
            sort_order INT NOT NULL DEFAULT 999999,
            description TEXT,
            directions TEXT,
            latitude NUMERIC DEFAULT NULL,
            longitude NUMERIC DEFAULT NULL,
            address VARCHAR(128) DEFAULT NULL,
            post_code VARCHAR(15) DEFAULT NULL,
            marker VARCHAR(45) DEFAULT NULL,
            show_on_menu BOOLEAN DEFAULT FALSE,
            alias_for VARCHAR(10) DEFAULT NULL,
            play_cricket_id BIGINT DEFAULT NULL,
            PRIMARY KEY(slug)
        );
