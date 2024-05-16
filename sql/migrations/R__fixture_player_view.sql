DROP VIEW IF EXISTS fixture_player;
CREATE VIEW fixture_player AS
SELECT f.id                         AS fixture_id,
       players.player_id,
       players.position,
       INITCAP(players.player_name) AS name,
       players.captain,
       players.wicket_keeper
FROM fixture f,
     jsonb_to_recordset(
             CASE
                 WHEN (detail ->> 'home_team_id')::INT = f.team_id THEN detail -> 'players' -> 0 -> 'home_team'
                 ELSE detail -> 'players' -> 1 -> 'away_team'
                 END
     )
         AS players(position INT,
                    player_name TEXT,
                    player_id INT,
                    captain BOOLEAN,
                    wicket_keeper BOOLEAN)
;