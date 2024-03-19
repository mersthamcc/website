CREATE
    TABLE
        member_filter(
            user_id VARCHAR(64) NOT NULL,
            categories JSONB NULL,
            years_of_birth JSONB NULL,
            genders JSONB NULL,
            PRIMARY KEY(user_id)
        );