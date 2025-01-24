DROP VIEW IF EXISTS member_summary;
CREATE OR REPLACE VIEW member_summary AS
SELECT m.id,
       m.owner_user_id,
       INITCAP(familyname.value::JSONB ->> 0) AS familyname,
       INITCAP(givenname.value::JSONB ->> 0)  AS givenname,
       m.registration_date                    AS first_registration_date,
       (
           dob.value::JSONB ->> 0
           )::DATE                            AS dob,
       CASE
           WHEN cat.key = 'junior' THEN EXTRACT(
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
           END                                AS age,
       CASE
           WHEN cat.key = 'junior' THEN 'U' || EXTRACT(
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
           END                                AS agegroup,
       gender.value::JSONB ->> 0              AS gender,
       subs.year                              AS most_recent_subscription,
       subs.added_date                        AS last_subs_date,
       subs.price                             AS last_subs_price,
       cat.key                                AS last_subs_category,
       payments.received                      AS received,
       payments.types                         AS payment_types,
       pl.description,
       declarations.value::JSONB              AS declarations,
       identifiers.list                       AS identifiers,
       m.uuid,
       apple_pass_serial_number.value         AS apple_pass_serial_number,
       google_pass_serial_number.value        AS google_pass_serial_number
FROM "member" m
         INNER JOIN(SELECT member_id,
                           MAX(YEAR) AS YEAR
                    FROM member_subscription
                    GROUP BY member_id) sub ON
    sub.member_id = m.id
         INNER JOIN member_subscription subs ON
    subs.member_id = sub.member_id
        AND subs.year = sub.year
         INNER JOIN pricelist_item pl ON
    pl.id = subs.pricelist_item_id
         INNER JOIN member_category cat ON
    cat.id = pl.category_id
         LEFT OUTER JOIN(SELECT order_id,
                                SUM(amount) AS received,
                                STRING_AGG(
                                        DISTINCT TYPE,
                                        ','
                                )           AS types
                         FROM payment
                         GROUP BY order_id) payments ON
    payments.order_id = subs.order_id
         LEFT OUTER JOIN member_attribute givenname ON
    m.id = givenname.member_id
        AND givenname.attribute_id = (SELECT id
                                      FROM attribute_definition
                                      WHERE KEY = 'given-name'
                                      LIMIT 1)
         LEFT OUTER JOIN member_attribute familyname ON
    m.id = familyname.member_id
        AND familyname.attribute_id = (SELECT id
                                       FROM attribute_definition
                                       WHERE KEY = 'family-name'
                                       LIMIT 1)
         LEFT OUTER JOIN member_attribute dob ON
    m.id = dob.member_id
        AND dob.attribute_id = (SELECT id
                                FROM attribute_definition
                                WHERE KEY = 'dob'
                                LIMIT 1)
         LEFT OUTER JOIN member_attribute gender ON
    m.id = gender.member_id
        AND gender.attribute_id = (SELECT id
                                   FROM attribute_definition
                                   WHERE KEY = 'gender'
                                   LIMIT 1)
         LEFT OUTER JOIN member_attribute declarations ON
    m.id = declarations.member_id
        AND declarations.attribute_id = (SELECT id
                                         FROM attribute_definition
                                         WHERE KEY = 'junior-declarations'
                                         LIMIT 1)
         LEFT OUTER JOIN(SELECT member_id,
                                JSONB_AGG(
                                        DISTINCT name
                                ) AS list
                         FROM member_identifier
                         GROUP BY member_id) identifiers ON
    m.id = identifiers.member_id
         LEFT OUTER JOIN member_identifier apple_pass_serial_number ON apple_pass_serial_number.member_id = m.id AND
                                                                       apple_pass_serial_number.name =
                                                                       'APPLE_PASS_SERIAL'
         LEFT OUTER JOIN member_identifier google_pass_serial_number ON google_pass_serial_number.member_id = m.id AND
                                                                        google_pass_serial_number.name =
                                                                        'GOOGLE_PASS_SERIAL'

WHERE m.cancelled IS NULL
ORDER BY familyname,
         givenname