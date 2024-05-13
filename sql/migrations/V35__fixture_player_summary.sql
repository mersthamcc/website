CREATE TABLE fixture_player_summary
(
    fixture_id INT,
    player_id  INT,
    runs       INT,
    out        BOOLEAN,
    dnb        BOOLEAN,
    balls      INT,
    wickets    INT,
    overs      INT,
    maidens    INT,
    catches    INT,
    PRIMARY KEY (fixture_id, player_id)
);