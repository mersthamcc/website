CREATE
    TABLE
        team(
            id INT NOT NULL,
            sort_order INT NOT NULL,
            slug TEXT NOT NULL,
            name TEXT NOT NULL,
            status TEXT NOT NULL,
            captain TEXT,
            PRIMARY KEY(id)
        );

CREATE
    TABLE
        fixture(
            id INT NOT NULL,
            team_id INT NOT NULL,
            opposition_team_id INT NULL,
            opposition TEXT NOT NULL,
            home_away VARCHAR(4),
            ground_id INT NULL,
            DATE DATE NULL,
            START TIME NULL,
            detail JSONB,
            PRIMARY KEY(id)
        );

CREATE
    INDEX IDX_FIXTURE_DATE ON
    fixture(DATE);

ALTER TABLE
    fixture ADD CONSTRAINT FK_FIXTURE_TEAM_ID FOREIGN KEY(team_id) REFERENCES team(id) NOT DEFERRABLE INITIALLY IMMEDIATE;
