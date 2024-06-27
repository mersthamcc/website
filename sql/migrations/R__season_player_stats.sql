DROP VIEW IF EXISTS fantasy_player_statistics;
CREATE OR REPLACE VIEW fantasy_player_statistics AS
SELECT
    DATE_PART('year', f.date) AS year,
    p.id,
    p.detail ->> 'name' AS name,
    COUNT(fp.fixture_id) AS matches,
    SUM(COALESCE(fp.runs, 0)) AS runs,
    SUM(COALESCE(fp.wickets, 0)) AS wickets,
    SUM(COALESCE(fp.catches, 0)) AS catches,
    SUM(COALESCE(fp.maidens, 0)) AS maidens,
    SUM(COALESCE(fp.conceded_runs, 0)) AS conceded_runs,
    SUM(FLOOR(COALESCE(fp.overs, 0))) AS overs,
    COUNT(fp.player_id) FILTER (WHERE COALESCE(fp.runs, 0) BETWEEN 50 AND 99) AS fifties,
    COUNT(fp.player_id) FILTER (WHERE COALESCE(fp.runs, 0) > 99 ) AS hundreds,
    COUNT(fp.player_id) FILTER (WHERE COALESCE(fp.runs, 0) = 0 AND "out" = TRUE) AS ducks,
    COUNT(fp.player_id) FILTER (WHERE fp.out = FALSE) AS not_out
FROM fixture_player_summary fp
         INNER JOIN fixture f ON fp.fixture_id = f.id
         INNER JOIN player p ON fp.player_id = p.id
         INNER JOIN team t ON f.team_id = t.id
WHERE t.include_in_selection = TRUE
  AND EXTRACT(isodow FROM f.date) = 6
  AND (f.detail ->> 'competition_type' = 'League'
    OR fp.fixture_id IN (6616890, 6593210))
GROUP BY 1, 2, 3
