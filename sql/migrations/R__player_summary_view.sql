DROP
    VIEW IF EXISTS player_summary;

CREATE
    VIEW player_summary AS SELECT
        p.id,
        p.detail ->> 'name' AS name,
        COUNT( last_year.id ) AS fixtures_last_year,
        COUNT( this_year.id ) AS fixtures_this_year
    FROM
        player p
    LEFT OUTER JOIN fixture last_year ON
        (
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
        )= DATE_PART(
            'YEAR',
            CURRENT_DATE
        )- 1
    LEFT OUTER JOIN fixture this_year ON
        (
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
        )= DATE_PART(
            'YEAR',
            CURRENT_DATE
        )
    GROUP BY
        p.id;