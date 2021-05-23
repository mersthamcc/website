INSERT INTO member_category (key)
VALUES ('adult'),
       ('junior'),
       ('disability'),
       ('social')
ON CONFLICT DO NOTHING;

INSERT INTO attribute_definition (key, type, choices)
VALUES ('given-name', 'String', NULL),
       ('family-name', 'String', NULL),
       ('school', 'String', NULL),
       ('dob', 'Date', NULL),
       ('parent-name-1', 'String', NULL),
       ('parent-name-2', 'String', NULL),
       ('parent-number-1', 'String', NULL),
       ('parent-number-2', 'String', NULL),
       ('parent-email-1', 'Email', NULL),
       ('parent-email-2', 'Email', NULL),
       ('address-1', 'String', NULL),
       ('address-2', 'String', NULL),
       ('city', 'String', NULL),
       ('county', 'String', NULL),
       ('postal-code', 'String', NULL),
       ('email', 'Email', NULL),
       ('phone', 'String', NULL),
       ('gender', 'Option', '[
         "MALE",
         "FEMALE",
         "NON-BINARY",
         "NOT-SPECIFIED"
       ]'::JSONB),
       ('skills', 'List', '[
         "BOWL",
         "BAT",
         "KEEPER"
       ]'::JSONB),
       ('preferred-days', 'List', '[
         "SATURDAY",
         "SUNDAY"
       ]'::JSONB),
       ('open-age-allowed', 'Boolean', NULL)
ON CONFLICT DO NOTHING;


INSERT INTO member_form_section (key)
VALUES ('contact'),
       ('parent'),
       ('address'),
       ('adult-cricket'),
       ('junior-cricket'),
       ('adult-basics'),
       ('junior-basics')
ON CONFLICT DO NOTHING;

INSERT INTO member_form_section_attribute (member_form_section_id, attribute_definition_id, sort_order, mandatory)
VALUES ((SELECT id FROM member_form_section WHERE key = 'junior-basics'),
        (SELECT id FROM attribute_definition WHERE key = 'given-name'), 10, TRUE),
       ((SELECT id FROM member_form_section WHERE key = 'junior-basics'),
        (SELECT id FROM attribute_definition WHERE key = 'family-name'), 20, TRUE),
       ((SELECT id FROM member_form_section WHERE key = 'junior-basics'),
        (SELECT id FROM attribute_definition WHERE key = 'dob'), 30, TRUE),
       ((SELECT id FROM member_form_section WHERE key = 'junior-basics'),
        (SELECT id FROM attribute_definition WHERE key = 'gender'), 40, TRUE),
       ((SELECT id FROM member_form_section WHERE key = 'junior-basics'),
        (SELECT id FROM attribute_definition WHERE key = 'school'), 50, TRUE),
       ((SELECT id FROM member_form_section WHERE key = 'adult-basics'),
        (SELECT id FROM attribute_definition WHERE key = 'given-name'), 10, TRUE),
       ((SELECT id FROM member_form_section WHERE key = 'adult-basics'),
        (SELECT id FROM attribute_definition WHERE key = 'family-name'), 20, TRUE),
       ((SELECT id FROM member_form_section WHERE key = 'adult-basics'),
        (SELECT id FROM attribute_definition WHERE key = 'gender'), 30, TRUE),
       ((SELECT id FROM member_form_section WHERE key = 'contact'),
        (SELECT id FROM attribute_definition WHERE key = 'phone'), 10, TRUE),
       ((SELECT id FROM member_form_section WHERE key = 'contact'),
        (SELECT id FROM attribute_definition WHERE key = 'email'), 20, TRUE),
       ((SELECT id FROM member_form_section WHERE key = 'parent'),
        (SELECT id FROM attribute_definition WHERE key = 'parent-name-1'), 10, TRUE),
       ((SELECT id FROM member_form_section WHERE key = 'parent'),
        (SELECT id FROM attribute_definition WHERE key = 'parent-email-1'), 20, TRUE),
       ((SELECT id FROM member_form_section WHERE key = 'parent'),
        (SELECT id FROM attribute_definition WHERE key = 'parent-number-1'), 30, TRUE),
       ((SELECT id FROM member_form_section WHERE key = 'parent'),
        (SELECT id FROM attribute_definition WHERE key = 'parent-name-2'), 40, FALSE),
       ((SELECT id FROM member_form_section WHERE key = 'parent'),
        (SELECT id FROM attribute_definition WHERE key = 'parent-email-2'), 50, FALSE),
       ((SELECT id FROM member_form_section WHERE key = 'parent'),
        (SELECT id FROM attribute_definition WHERE key = 'parent-number-2'), 60, FALSE),
       ((SELECT id FROM member_form_section WHERE key = 'address'),
        (SELECT id FROM attribute_definition WHERE key = 'address-1'), 10, TRUE),
       ((SELECT id FROM member_form_section WHERE key = 'address'),
        (SELECT id FROM attribute_definition WHERE key = 'address-2'), 20, FALSE),
       ((SELECT id FROM member_form_section WHERE key = 'address'),
        (SELECT id FROM attribute_definition WHERE key = 'city'), 30, TRUE),
       ((SELECT id FROM member_form_section WHERE key = 'address'),
        (SELECT id FROM attribute_definition WHERE key = 'county'), 40, TRUE),
       ((SELECT id FROM member_form_section WHERE key = 'address'),
        (SELECT id FROM attribute_definition WHERE key = 'postal-code'), 50, TRUE),
       ((SELECT id FROM member_form_section WHERE key = 'adult-cricket'),
        (SELECT id FROM attribute_definition WHERE key = 'skills'), 10, FALSE),
       ((SELECT id FROM member_form_section WHERE key = 'adult-cricket'),
        (SELECT id FROM attribute_definition WHERE key = 'preferred-days'), 20, FALSE),
       ((SELECT id FROM member_form_section WHERE key = 'junior-cricket'),
        (SELECT id FROM attribute_definition WHERE key = 'skills'), 10, FALSE),
       ((SELECT id FROM member_form_section WHERE key = 'junior-cricket'),
        (SELECT id FROM attribute_definition WHERE key = 'preferred-days'), 20, FALSE),
       ((SELECT id FROM member_form_section WHERE key = 'junior-cricket'),
        (SELECT id FROM attribute_definition WHERE key = 'open-age-allowed'), 30, FALSE)
ON CONFLICT (member_form_section_id, attribute_definition_id) DO
    UPDATE SET sort_order = EXCLUDED.sort_order,
               mandatory  = EXCLUDED.mandatory
;

INSERT INTO member_category_form_section (member_category_id, member_form_section_id, sort_order, show_on_registration)
VALUES ((SELECT id FROM member_category WHERE key = 'junior'),
        (SELECT id FROM member_form_section WHERE key = 'junior-basics'), 10, TRUE),
       ((SELECT id FROM member_category WHERE key = 'junior'),
        (SELECT id FROM member_form_section WHERE key = 'parent'), 20, TRUE),
       ((SELECT id FROM member_category WHERE key = 'junior'),
        (SELECT id FROM member_form_section WHERE key = 'junior-cricket'), 30, TRUE),
       ((SELECT id FROM member_category WHERE key = 'adult'),
        (SELECT id FROM member_form_section WHERE key = 'adult-basics'), 10, TRUE),
       ((SELECT id FROM member_category WHERE key = 'adult'),
        (SELECT id FROM member_form_section WHERE key = 'adult-cricket'), 20, TRUE),
       ((SELECT id FROM member_category WHERE key = 'disability'),
        (SELECT id FROM member_form_section WHERE key = 'junior-basics'), 10, TRUE),
       ((SELECT id FROM member_category WHERE key = 'disability'),
        (SELECT id FROM member_form_section WHERE key = 'parent'), 20, TRUE),
       ((SELECT id FROM member_category WHERE key = 'disability'),
        (SELECT id FROM member_form_section WHERE key = 'address'), 30, TRUE),
       ((SELECT id FROM member_category WHERE key = 'disability'),
        (SELECT id FROM member_form_section WHERE key = 'junior-cricket'), 40, TRUE),
       ((SELECT id FROM member_category WHERE key = 'social'),
        (SELECT id FROM member_form_section WHERE key = 'adult-basics'), 10, TRUE),
       ((SELECT id FROM member_category WHERE key = 'social'),
        (SELECT id FROM member_form_section WHERE key = 'contact'), 20, TRUE)
ON CONFLICT DO NOTHING;

INSERT INTO pricelist_item (id, category_id, min_age, max_age, description, includes_match_fees)
VALUES (1, (SELECT id FROM member_category WHERE key = 'junior'), 5, 7, 'Juniors (under 7)', NULL),
       (2, (SELECT id FROM member_category WHERE key = 'junior'), 8, 15, 'Juniors (U8 - U15)', NULL),
       (3, (SELECT id FROM member_category WHERE key = 'adult'), 16, NULL, 'Students (U16 and older)', FALSE),
       (4, (SELECT id FROM member_category WHERE key = 'adult'), 16, NULL, 'Students (inclusive of match fees)', FALSE),
       (5, (SELECT id FROM member_category WHERE key = 'adult'), 16, NULL, 'Adults', FALSE),
       (6, (SELECT id FROM member_category WHERE key = 'adult'), 16, NULL, 'Adults (inclusive of match fees)', TRUE),
       (7, (SELECT id FROM member_category WHERE key = 'adult'), 15, NULL, 'Ladies (15 years and older)', NULL),
       (8, (SELECT id FROM member_category WHERE key = 'disability'), 5, NULL, 'Disability (All Ages)', NULL),
       (9, (SELECT id FROM member_category WHERE key = 'social'), 16, NULL, 'Social', NULL)
ON CONFLICT DO NOTHING;

INSERT INTO pricelist (pricelist_item_id, date_from, date_to, price)
VALUES (1, '2021-01-01', '2021-12-31', 40.00),
       (2, '2021-01-01', '2021-12-31', 70.00),
       (3, '2021-01-01', '2021-05-31', 50.00),
       (3, '2021-06-01', '2021-12-31', 60.00),
       (4, '2021-01-01', '2021-05-31', 155.00),
       (5, '2021-01-01', '2021-05-31', 90.00),
       (5, '2021-06-01', '2021-12-31', 110.00),
       (6, '2021-01-01', '2021-05-31', 300.00),
       (7, '2021-01-01', '2021-12-31', 40.00),
       (8, '2021-01-01', '2021-12-31', 40.00),
       (9, '2021-01-01', '2021-12-31', 25.00)
ON CONFLICT DO NOTHING;
