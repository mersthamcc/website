DROP VIEW IF EXISTS member_attendance_summary;
CREATE OR REPLACE VIEW member_attendance_summary AS
SELECT
    a.id,
    a."time",
    a.member_id,
    a.event,
    CASE
        WHEN a.member_id IS NULL THEN a.non_member_name
        ELSE INITCAP(givenname.value::JSONB ->> 0) || ' ' || INITCAP(familyname.value::JSONB ->> 0)
        END AS full_name,
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
        WHEN a.member_id IS NULL THEN 'Unregistered at time'
        ELSE cat.key
        END AS agegroup,
    sub.year AS registration_year
FROM member_attendance a
         LEFT OUTER JOIN member m ON m.id = a.member_id
         LEFT OUTER JOIN(SELECT member_id, MAX(YEAR) AS YEAR
                         FROM member_subscription
                         GROUP BY member_id) sub ON sub.member_id = a.member_id
         LEFT OUTER JOIN member_subscription subs ON subs.member_id = sub.member_id AND subs.year = sub.year
         LEFT OUTER JOIN "order" ord ON subs.order_id = ord.id AND subs.year = sub.year
         LEFT OUTER JOIN pricelist_item pl ON pl.id = subs.pricelist_item_id
         LEFT OUTER JOIN member_category cat ON cat.id = pl.category_id
         LEFT OUTER JOIN member_attribute givenname ON m.id = givenname.member_id
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
ORDER BY "time"
;