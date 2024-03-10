CREATE
    OR REPLACE VIEW member_summary AS SELECT
        m.id,
        familyname.value::JSONB ->> 0 AS familyname,
        givenname.value::JSONB ->> 0 AS givenname,
        m.registration_date AS first_registration_date,
        dob.value::JSONB ->> 0 AS dob,
        CASE
            WHEN dob.value IS NULL THEN NULL
            ELSE 'U' || EXTRACT(
                YEAR
            FROM
                AGE(
                    format(
                        '%s-%s-%s',
                        EXTRACT(
                            YEAR
                        FROM
                            CURRENT_DATE
                        ),
                        08,
                        31
                    )::DATE,
                    (
                        dob.value::JSONB ->> 0
                    )::DATE
                )
            )
        END AS agegroup,
        subs.year AS most_recent_subscription,
        subs.added_date AS last_subs_date,
        subs.price AS last_subs_price,
        cat.key AS last_subs_category,
        payments.received AS received,
        payments.types AS payment_types,
        pl.description,
        declarations.value::JSONB AS declarations
    FROM
        MEMBER m
    INNER JOIN(
            SELECT
                member_id,
                MAX( YEAR ) AS YEAR
            FROM
                member_subscription
            GROUP BY
                member_id
        ) sub ON
        sub.member_id = m.id
    INNER JOIN member_subscription subs ON
        subs.member_id = sub.member_id
        AND subs.year = sub.year
    INNER JOIN pricelist_item pl ON
        pl.id = subs.pricelist_item_id
    INNER JOIN member_category cat ON
        cat.id = pl.category_id
    LEFT OUTER JOIN(
            SELECT
                order_id,
                SUM( amount ) AS received,
                STRING_AGG(
                    DISTINCT TYPE,
                    ','
                ) AS types
            FROM
                payment
            GROUP BY
                order_id
        ) payments ON
        payments.order_id = subs.order_id
    LEFT OUTER JOIN member_attribute givenname ON
        m.id = givenname.member_id
        AND givenname.attribute_id = 1
    LEFT OUTER JOIN member_attribute familyname ON
        m.id = familyname.member_id
        AND familyname.attribute_id = 2
    LEFT OUTER JOIN member_attribute dob ON
        m.id = dob.member_id
        AND dob.attribute_id = 4
    LEFT OUTER JOIN member_attribute declarations ON
        m.id = declarations.member_id
        AND declarations.attribute_id = 68
    WHERE
        m.cancelled IS NULL
    ORDER BY
        familyname,
        givenname