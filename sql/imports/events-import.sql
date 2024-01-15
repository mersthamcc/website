INSERT
    INTO
        event(
            id,
            event_date,
            title,
            PATH,
            uuid,
            location,
            body
        ) SELECT
            CAST(
                value ->> 'id' AS INT
            ) AS id,
            to_timestamp(
                value ->> 'event_date',
                'YYYY-MM-DD HH24:MI:SS.US'
            ) AS event_date,
            value ->> 'name' AS title,
            CONCAT(
                '/',
                to_char(
                    to_timestamp(
                        value ->> 'event_date',
                        'YYYY-MM-DD HH24:MI:SS.US'
                    ),
                    'YYYY'
                ),
                '/',
                to_char(
                    to_timestamp(
                        value ->> 'event_date',
                        'YYYY-MM-DD HH24:MI:SS.US'
                    ),
                    'MM'
                ),
                '/',
                to_char(
                    to_timestamp(
                        value ->> 'event_date',
                        'YYYY-MM-DD HH24:MI:SS.US'
                    ),
                    'DD'
                ),
                '/',
                slugify(
                    value ->> 'name'
                )
            ) AS PATH,
            gen_random_uuid()::TEXT AS unid,
            value ->> 'location' AS location,
            value ->> 'details' AS body
        FROM
            json_array_elements(
                CAST(
                    convert_from(
                        lo_get(25085),
                        'utf-8'
                    ) AS JSON
                )
            );
