CREATE
    TABLE
        team(
            id INT,
            slug TEXT NOT NULL,
            details JSONB,
            PRIMARY KEY(id)
        );

CREATE
    UNIQUE INDEX IDX_UNIQUE_TEAM_SLUG ON
    team(slug);

CREATE
    TABLE
        fixture(
            id INT,
            team_id INT NOT NULL,
            DATE DATE,
            detail JSONB,
            PRIMARY KEY(id)
        );

CREATE
    INDEX IDX_FIXTURE_DATE ON
    fixture(DATE);

ALTER TABLE
    fixture ADD CONSTRAINT FK_FIXTURE_TEAM_ID FOREIGN KEY(team_id) REFERENCES team(id) NOT DEFERRABLE INITIALLY IMMEDIATE;
