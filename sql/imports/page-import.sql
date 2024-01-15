INSERT
    INTO
        static_page(
            slug,
            content,
            title,
            sort_order
        ) SELECT
            value ->> 'name' AS slug,
            value ->> 'content' AS content,
            value ->> 'title' AS title,
            CAST(
                value ->> 'sortorder' AS INT
            ) AS sort_order
        FROM
            json_array_elements(
                CAST(
                    convert_from(
                        lo_get(25065),
                        'utf-8'
                    ) AS JSON
                )
            );
