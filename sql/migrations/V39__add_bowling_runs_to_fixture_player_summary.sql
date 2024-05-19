ALTER TABLE fixture_player_summary
    ADD COLUMN conceded_runs INT,
    ALTER COLUMN overs TYPE DECIMAL
;