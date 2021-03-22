INSERT INTO member_category (key)
VALUES ('adult'),
       ('junior'),
       ('disability'),
       ('social')
ON CONFLICT DO NOTHING;

INSERT INTO attribute_definition (key, type, choices)
VALUES ('school', 'String', NULL),
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
       ('adult-personal'),
       ('junior-personal')
ON CONFLICT DO NOTHING;

INSERT INTO member_form_section_attribute (member_form_section_id, attribute_definition_id, sort_order)
VALUES ((SELECT id FROM member_form_section WHERE key = 'junior-personal'),
        (SELECT id FROM attribute_definition WHERE key = 'dob'), 10),
       ((SELECT id FROM member_form_section WHERE key = 'junior-personal'),
        (SELECT id FROM attribute_definition WHERE key = 'gender'), 20),
       ((SELECT id FROM member_form_section WHERE key = 'junior-personal'),
        (SELECT id FROM attribute_definition WHERE key = 'school'), 30),
       ((SELECT id FROM member_form_section WHERE key = 'adult-personal'),
        (SELECT id FROM attribute_definition WHERE key = 'gender'), 10),
       ((SELECT id FROM member_form_section WHERE key = 'contact'),
        (SELECT id FROM attribute_definition WHERE key = 'phone'), 10),
       ((SELECT id FROM member_form_section WHERE key = 'contact'),
        (SELECT id FROM attribute_definition WHERE key = 'email'), 20),
       ((SELECT id FROM member_form_section WHERE key = 'parent'),
        (SELECT id FROM attribute_definition WHERE key = 'parent-name-1'), 10),
       ((SELECT id FROM member_form_section WHERE key = 'parent'),
        (SELECT id FROM attribute_definition WHERE key = 'parent-email-1'), 20),
       ((SELECT id FROM member_form_section WHERE key = 'parent'),
        (SELECT id FROM attribute_definition WHERE key = 'parent-number-1'), 30),
       ((SELECT id FROM member_form_section WHERE key = 'parent'),
        (SELECT id FROM attribute_definition WHERE key = 'parent-name-2'), 40),
       ((SELECT id FROM member_form_section WHERE key = 'parent'),
        (SELECT id FROM attribute_definition WHERE key = 'parent-email-2'), 50),
       ((SELECT id FROM member_form_section WHERE key = 'parent'),
        (SELECT id FROM attribute_definition WHERE key = 'parent-number-2'), 60),
       ((SELECT id FROM member_form_section WHERE key = 'address'),
        (SELECT id FROM attribute_definition WHERE key = 'address-1'), 10),
       ((SELECT id FROM member_form_section WHERE key = 'address'),
        (SELECT id FROM attribute_definition WHERE key = 'address-2'), 20),
       ((SELECT id FROM member_form_section WHERE key = 'address'),
        (SELECT id FROM attribute_definition WHERE key = 'city'), 30),
       ((SELECT id FROM member_form_section WHERE key = 'address'),
        (SELECT id FROM attribute_definition WHERE key = 'county'), 40),
       ((SELECT id FROM member_form_section WHERE key = 'address'),
        (SELECT id FROM attribute_definition WHERE key = 'postal-code'), 50),
       ((SELECT id FROM member_form_section WHERE key = 'adult-cricket'),
        (SELECT id FROM attribute_definition WHERE key = 'skills'), 10),
       ((SELECT id FROM member_form_section WHERE key = 'adult-cricket'),
        (SELECT id FROM attribute_definition WHERE key = 'preferred-days'), 20),
       ((SELECT id FROM member_form_section WHERE key = 'junior-cricket'),
        (SELECT id FROM attribute_definition WHERE key = 'skills'), 10),
       ((SELECT id FROM member_form_section WHERE key = 'junior-cricket'),
        (SELECT id FROM attribute_definition WHERE key = 'preferred-days'), 20),
       ((SELECT id FROM member_form_section WHERE key = 'junior-cricket'),
        (SELECT id FROM attribute_definition WHERE key = 'open-age-allowed'), 30)
ON CONFLICT DO NOTHING;

INSERT INTO member_category_form_section (member_category_id, member_form_section_id, sort_order)
VALUES ((SELECT id FROM member_category WHERE key = 'junior'),
        (SELECT id FROM member_form_section WHERE key = 'junior-personal'), 10),
       ((SELECT id FROM member_category WHERE key = 'junior'),
        (SELECT id FROM member_form_section WHERE key = 'parent'), 20),
       ((SELECT id FROM member_category WHERE key = 'junior'),
        (SELECT id FROM member_form_section WHERE key = 'junior-cricket'), 30),
       ((SELECT id FROM member_category WHERE key = 'adult'),
        (SELECT id FROM member_form_section WHERE key = 'adult-personal'), 10),
       ((SELECT id FROM member_category WHERE key = 'adult'),
        (SELECT id FROM member_form_section WHERE key = 'adult-cricket'), 20),
       ((SELECT id FROM member_category WHERE key = 'disability'),
        (SELECT id FROM member_form_section WHERE key = 'junior-personal'), 10),
       ((SELECT id FROM member_category WHERE key = 'disability'),
        (SELECT id FROM member_form_section WHERE key = 'parent'), 20),
       ((SELECT id FROM member_category WHERE key = 'disability'),
        (SELECT id FROM member_form_section WHERE key = 'address'), 30),
       ((SELECT id FROM member_category WHERE key = 'disability'),
        (SELECT id FROM member_form_section WHERE key = 'junior-cricket'), 40),
       ((SELECT id FROM member_category WHERE key = 'social'),
        (SELECT id FROM member_form_section WHERE key = 'contact'), 10)
ON CONFLICT DO NOTHING;

INSERT INTO pricelist (date_from, date_to, category_id, min_age, max_age, description, price, additional_unit_price, includes_match_fees)
VALUES ('2021-01-01', '2021-12-31', (SELECT id FROM member_category WHERE key = 'junior'), 5, 7, 'Juniors (under 7)', 40.00, NULL, NULL),
       ('2021-01-01', '2021-12-31', (SELECT id FROM member_category WHERE key = 'junior'), 8, 15, 'Juniors (U8 - U15)', 70.00, 60.00, NULL),
       ('2021-01-01', '2021-05-31', (SELECT id FROM member_category WHERE key = 'adult'), 16, NULL, 'Students (16 years and older)', 50.00, NULL, FALSE),
       ('2021-06-01', '2021-12-31', (SELECT id FROM member_category WHERE key = 'adult'), 16, NULL, 'Students (16 years and older)', 60.00, NULL, FALSE),
       ('2021-01-01', '2021-05-31', (SELECT id FROM member_category WHERE key = 'adult'), 16, NULL, 'Adults', 90.00, NULL, FALSE),
       ('2021-06-01', '2021-12-31', (SELECT id FROM member_category WHERE key = 'adult'), 16, NULL, 'Adults', 100.00, NULL, FALSE),
       ('2021-06-01', '2021-12-31', (SELECT id FROM member_category WHERE key = 'adult'), 16, NULL, 'Adults (inclusive of match fees)', 300.00, NULL, TRUE),
       ('2021-01-01', '2021-12-31', (SELECT id FROM member_category WHERE key = 'adult'), 15, NULL, 'Ladies (15 years and older)', 40.00, NULL, NULL),
       ('2021-01-01', '2021-12-31', (SELECT id FROM member_category WHERE key = 'disability'), 5, NULL, 'Disability (All Ages)', 40.00, NULL, NULL),
       ('2021-01-01', '2021-12-31', (SELECT id FROM member_category WHERE key = 'social'), 16, NULL, 'Social', 25.00, NULL, NULL)
ON CONFLICT DO NOTHING;
