INSERT
    INTO
        contact(
            id,
            category_id,
            POSITION,
            slug,
            name,
            sort_order
        ) SELECT
            CAST(
                value ->> 'id' AS INT
            ) AS id,
            1 AS category_id,
            value ->> 'position' AS POSITION,
            slugify(
                value ->> 'position'
            ) AS slug,
            value ->> 'name' AS name,
            CAST(
                value ->> 'displayorder' AS INT
            ) AS sort_order
        FROM
            json_array_elements(
                CAST(
                    convert_from(
                        lo_get(25091),
                        'utf-8'
                    ) AS JSON
                )
            );

INSERT
    INTO
        contact_method(
            contact_id,
            METHOD,
            value
        ) SELECT
            CAST(
                value ->> 'id' AS INT
            ) AS contact_id,
            'PHONE',
            value ->> 'phone'
        FROM
            json_array_elements(
                CAST(
                    convert_from(
                        lo_get(25091),
                        'utf-8'
                    ) AS JSON
                )
            );

INSERT
    INTO
        contact_method(
            contact_id,
            METHOD,
            value
        ) SELECT
            CAST(
                value ->> 'id' AS INT
            ) AS contact_id,
            'EMAIL',
            value ->> 'email'
        FROM
            json_array_elements(
                CAST(
                    convert_from(
                        lo_get(25091),
                        'utf-8'
                    ) AS JSON
                )
            );
