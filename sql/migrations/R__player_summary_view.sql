DROP
    VIEW IF EXISTS player_summary;

CREATE VIEW player_summary AS
SELECT p.id,
       p.detail ->> 'name' AS name,
       (SELECT COUNT(*)
        FROM fixture last_year
        WHERE (
            last_year.detail -> 'players' -> 0 -> 'home_team' @> json_build_array(
                    json_build_object(
                            'player_id',
                            p.id
                    )
                                                                 )::JSONB
                OR last_year.detail -> 'players' -> 1 -> 'away_team' @> json_build_array(
                    json_build_object(
                            'player_id',
                            p.id
                    )
                                                                        )::JSONB
            )
          AND DATE_PART(
                      'YEAR',
                      last_year.date
              ) = DATE_PART(
                          'YEAR',
                          CURRENT_DATE
                  ) - 1)   AS fixtures_last_year,
       (SELECT COUNT(*)
        FROM fixture this_year
        WHERE (
            this_year.detail -> 'players' -> 0 -> 'home_team' @> json_build_array(
                    json_build_object(
                            'player_id',
                            p.id
                    )
                                                                 )::JSONB
                OR this_year.detail -> 'players' -> 1 -> 'away_team' @> json_build_array(
                    json_build_object(
                            'player_id',
                            p.id
                    )
                                                                        )::JSONB
            )
          AND DATE_PART(
                      'YEAR',
                      this_year.date
              ) = DATE_PART(
                      'YEAR',
                      CURRENT_DATE
                  ))       AS fixtures_this_year,
       (SELECT MIN(date)
        FROM fixture total_fixtures
        WHERE (
                  total_fixtures.detail -> 'players' -> 0 -> 'home_team' @> json_build_array(
                          json_build_object(
                                  'player_id',
                                  p.id
                          ))::JSONB
                      OR total_fixtures.detail -> 'players' -> 1 -> 'away_team' @> json_build_array(
                          json_build_object(
                                  'player_id',
                                  p.id
                          ))::JSONB
                  ))       AS earliest_date
FROM player p;

