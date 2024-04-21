SELECT
    p.date AS PaymentDate,
    CASE WHEN p.type = 'stripe' THEN REPLACE(p.reference,'pi_', '') ELSE p.reference END AS PaymentReference,
    p.amount AS PaymentAmount,
    p.processing_fees AS PaymentFees,
    p.type AS PaymentType,
    CONCAT('WEB-', o.id) AS SubsReference,
    o.owner_user_id AS RegistrationCreatedBy,
    o.owner_user_id AS RegistrationCreatedAddress,
    o.create_date AS RegistrationDate,
    totals.price AS RegistrationTotalAmount,
    COALESCE(totals.count, 0) AS TotalMembers,
    COALESCE(junior.count, 0) AS JuniorMembers,
    COALESCE(adult.count, 0) AS AdultMembers,
    COALESCE(social.count, 0) AS SocialMembers,
    (COALESCE(junior.count::FLOAT, 0.00)/totals.count) * 100 AS JuniorPercentage,
    (COALESCE(adult.count::FLOAT, 0.00)/totals.count) * 100 As AdultPercentage,
    (COALESCE(social.count::FLOAT, 0.00)/totals.count) * 100 AS SocialPercentage,
    (COALESCE(junior.count::FLOAT, 0.00)/totals.count) * p.amount AS JuniorAllocation,
    (COALESCE(adult.count::FLOAT, 0.00)/totals.count) * p.amount AS AdultAllocation,
    (COALESCE(social.count::FLOAT, 0.00)/totals.count) * p.amount AS SocialAllocation,
    p.accounting_id,
    p.fees_accounting_id
FROM payment p
         INNER JOIN "order" o ON o.id = p.order_id
         LEFT OUTER JOIN ( SELECT s.order_id AS id,COUNT(*) AS count, SUM(s.price) AS price FROM member_subscription s GROUP BY s.order_id ) totals ON totals.id = o.id
         LEFT OUTER JOIN (
    SELECT
        s.order_id AS id,
        COUNT(*) AS count
    FROM member_subscription s
             INNER JOIN pricelist_item pl ON pl.id = s.pricelist_item_id
             INNER JOIN member_category c ON c.id = pl.category_id
    WHERE c.key = 'adult'
    GROUP BY s.order_id ) adult ON adult.id = o.id
         LEFT OUTER JOIN (
    SELECT
        s.order_id AS id,
        COUNT(*) AS count
    FROM member_subscription s
             INNER JOIN pricelist_item pl ON pl.id = s.pricelist_item_id
             INNER JOIN member_category c ON c.id = pl.category_id
    WHERE c.key IN ('junior', 'disability')
    GROUP BY s.order_id ) junior ON junior.id = o.id
         LEFT OUTER JOIN (
    SELECT
        s.order_id AS id,
        COUNT(*) AS count
    FROM member_subscription s
             INNER JOIN pricelist_item pl ON pl.id = s.pricelist_item_id
             INNER JOIN member_category c ON c.id = pl.category_id
    WHERE c.key = 'social'
    GROUP BY s.order_id ) social ON social.id = o.id
WHERE p.date BETWEEN '2024-01-01' AND '2024-02-29'
ORDER BY p.date, p.reference