\lo_import '../news-export.json';

INSERT
    INTO
        news(
            id,
            uuid,
            title,
            author,
            created_date,
            publish_date,
            PATH,
            draft,
            body
        ) SELECT
            CAST(
                value ->> 'id' AS INT
            ) AS id,
            COALESCE(
                value ->> 'unid',
                gen_random_uuid()::TEXT
            ) AS unid,
            value ->> 'title' AS title,
            value ->> 'author' AS author,
            to_timestamp(
                value ->> 'createDate',
                'YYYY-MM-DD HH24:MI:SS.US'
            ) AS createDate,
            to_timestamp(
                value ->> 'publishDate',
                'YYYY-MM-DD HH24:MI:SS.US'
            ) AS publishDate,
            CONCAT(
                '/',
                to_char(
                    to_timestamp(
                        value ->> 'createDate',
                        'YYYY-MM-DD HH24:MI:SS.US'
                    ),
                    'YYYY'
                ),
                '/',
                to_char(
                    to_timestamp(
                        value ->> 'createDate',
                        'YYYY-MM-DD HH24:MI:SS.US'
                    ),
                    'MM'
                ),
                '/',
                to_char(
                    to_timestamp(
                        value ->> 'createDate',
                        'YYYY-MM-DD HH24:MI:SS.US'
                    ),
                    'DD'
                ),
                '/',
                slugify(
                    value ->> 'title'
                )
            ) AS PATH,
            FALSE AS draft,
            value ->> 'body' AS body
        FROM
            json_array_elements(
                CAST(
                    convert_from(
                        lo_get(16736),
                        'utf-8'
                    ) AS JSON
                )
            )
        WHERE
            value ->> 'createDate' IS NOT NULL;
