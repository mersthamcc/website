INSERT
    INTO
        venue(
            slug,
            name,
            sort_order,
            description,
            directions,
            latitude,
            longitude,
            address,
            marker,
            show_on_menu,
            alias_for,
            play_cricket_id
        ) SELECT
            value ->> 'key' AS slug,
            value ->> 'name' AS content,
            CAST(
                value ->> 'sortorder' AS INT
            ) AS sort_order,
            value ->> 'description' AS description,
            value ->> 'directions' AS directions,
            CAST(
                value ->> 'latitude' AS NUMERIC
            ) AS latitude,
            CAST(
                value ->> 'longitude' AS NUMERIC
            ) AS longitude,
            value ->> 'address' AS address,
            value ->> 'marker' AS marker,
            CAST(
                value ->> 'showonmenu' AS BOOLEAN
            ) AS show_on_menu,
            value ->> 'aliasfor' AS alias_for,
            CAST(
                value ->> 'playcricketid' AS INT
            ) AS play_cricket_id
        FROM
            json_array_elements(
                CAST(
                    convert_from(
                        lo_get(25078),
                        'utf-8'
                    ) AS JSON
                )
            );
