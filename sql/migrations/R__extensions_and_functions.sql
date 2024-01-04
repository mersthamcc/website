CREATE
    EXTENSION IF NOT EXISTS "pgcrypto";

CREATE
    EXTENSION IF NOT EXISTS "unaccent";

CREATE
    OR REPLACE FUNCTION slugify(
        "value" TEXT
    ) RETURNS TEXT AS $$ WITH "unaccented" AS(
        SELECT
            unaccent("value") AS "value"
    ),
    "lowercase" AS(
        SELECT
            LOWER( "value" ) AS "value"
        FROM
            "unaccented"
    ),
    "hyphenated" AS(
        SELECT
            regexp_replace(
                "value",
                '[^a-z0-9\\-_]+',
                '-',
                'gi'
            ) AS "value"
        FROM
            "lowercase"
    ),
    "trimmed" AS(
        SELECT
            regexp_replace(
                regexp_replace(
                    "value",
                    '\\-+$',
                    ''
                ),
                '^\\-',
                ''
            ) AS "value"
        FROM
            "hyphenated"
    ) SELECT
        "value"
    FROM
        "trimmed";

$$ LANGUAGE SQL STRICT IMMUTABLE;
