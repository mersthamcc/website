CREATE
    TABLE
        league(
            id INT NOT NULL,
            name TEXT NOT NULL,
            last_update TIMESTAMP,
            "table" JSONB,
            PRIMARY KEY(id)
        );