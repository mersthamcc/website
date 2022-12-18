CREATE
    TABLE
        player(
            id INT NOT NULL,
            detail JSONB NOT NULL,
            PRIMARY KEY(id)
        );

ALTER TABLE
    team ADD COLUMN captain_id INT NULL,
    DROP
        COLUMN captain;

ALTER TABLE
    team ADD CONSTRAINT FK_TEAM_CAPTAIN_PLAYER_ID FOREIGN KEY(captain_id) REFERENCES player(id) NOT DEFERRABLE INITIALLY IMMEDIATE;
