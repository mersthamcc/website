INSERT
INTO member_category("key",
                     registration_code,
                     sort_order)
VALUES ('adult',
        NULL,
        20),
       ('junior',
        '${junior_code}',
        10),
       ('disability',
        NULL,
        30),
       ('social',
        NULL,
        40),
       ('honorary',
        '${honorary_code}',
        50)
ON CONFLICT("key") DO UPDATE
    SET registration_code = EXCLUDED.registration_code,
        sort_order        = EXCLUDED.sort_order;

INSERT
INTO attribute_definition(KEY,
                          TYPE,
                          choices)
VALUES ('given-name',
        'String',
        NULL),
       ('family-name',
        'String',
        NULL),
       ('school',
        'String',
        NULL),
       ('dob',
        'Date',
        NULL),
       ('parent-name-1',
        'String',
        NULL),
       ('parent-name-2',
        'String',
        NULL),
       ('parent-number-1',
        'String',
        NULL),
       ('parent-number-2',
        'String',
        NULL),
       ('parent-email-1',
        'Email',
        NULL),
       ('parent-email-2',
        'Email',
        NULL),
       ('address-1',
        'String',
        NULL),
       ('address-2',
        'String',
        NULL),
       ('city',
        'String',
        NULL),
       ('county',
        'String',
        NULL),
       ('postal-code',
        'String',
        NULL),
       ('email',
        'Email',
        NULL),
       ('phone',
        'String',
        NULL),
       ('gender',
        'Option',
        '[
          "MALE",
          "FEMALE"
        ]'::JSONB),
       ('skills',
        'List',
        '[
          "BOWL",
          "BAT",
          "KEEPER"
        ]'::JSONB),
       ('preferred-days',
        'List',
        '[
          "SATURDAY",
          "SUNDAY"
        ]'::JSONB),
       ('open-age-allowed',
        'Boolean',
        NULL),
       ('medical-conditions',
        'String',
        NULL),
       ('emergency-contact-name',
        'String',
        NULL),
       ('emergency-contact-relationship',
        'String',
        NULL),
       ('emergency-contact-phone',
        'String',
        NULL),
       ('junior-declarations',
        'List',
        '[
          "ACCEPT-DECLARATION",
          "ACCEPT-UNACCOMPANIED",
          "OPENAGE",
          "PHOTOS-COACHING",
          "PHOTOS-MARKETING"
        ]'::JSONB)
ON CONFLICT(KEY) DO UPDATE
    SET TYPE    = EXCLUDED.type,
        choices = EXCLUDED.choices;

INSERT
INTO member_form_section(KEY)
VALUES ('contact'),
       ('parent'),
       ('address'),
       ('adult-cricket'),
       ('junior-cricket'),
       ('adult-basics'),
       ('social-basics'),
       ('disability-basics'),
       ('junior-basics'),
       ('emergency-contact'),
       ('medical-conditions'),
       ('junior-policy')
ON CONFLICT DO NOTHING;

INSERT
INTO member_form_section_attribute(member_form_section_id,
                                   attribute_definition_id,
                                   sort_order,
                                   mandatory)
VALUES ((SELECT id
         FROM member_form_section
         WHERE KEY = 'junior-basics'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'given-name'),
        10,
        TRUE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'junior-basics'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'family-name'),
        20,
        TRUE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'junior-basics'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'dob'),
        30,
        TRUE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'junior-basics'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'gender'),
        40,
        TRUE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'junior-basics'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'school'),
        50,
        FALSE),

       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'disability-basics'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'given-name'),
        10,
        TRUE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'disability-basics'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'family-name'),
        20,
        TRUE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'disability-basics'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'dob'),
        30,
        TRUE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'disability-basics'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'gender'),
        40,
        TRUE),

       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'adult-basics'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'given-name'),
        10,
        TRUE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'adult-basics'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'family-name'),
        20,
        TRUE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'adult-basics'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'dob'),
        25,
        TRUE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'adult-basics'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'gender'),
        30,
        TRUE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'contact'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'phone'),
        10,
        TRUE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'contact'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'email'),
        20,
        TRUE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'parent'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'parent-name-1'),
        10,
        TRUE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'parent'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'parent-email-1'),
        20,
        TRUE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'parent'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'parent-number-1'),
        30,
        TRUE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'parent'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'parent-name-2'),
        40,
        FALSE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'parent'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'parent-email-2'),
        50,
        FALSE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'parent'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'parent-number-2'),
        60,
        FALSE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'address'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'address-1'),
        10,
        TRUE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'address'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'address-2'),
        20,
        FALSE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'address'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'city'),
        30,
        TRUE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'address'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'county'),
        40,
        TRUE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'address'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'postal-code'),
        50,
        TRUE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'adult-cricket'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'skills'),
        10,
        FALSE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'adult-cricket'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'preferred-days'),
        20,
        FALSE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'junior-cricket'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'skills'),
        10,
        FALSE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'junior-cricket'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'preferred-days'),
        20,
        FALSE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'emergency-contact'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'emergency-contact-name'),
        10,
        TRUE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'emergency-contact'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'emergency-contact-phone'),
        20,
        TRUE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'emergency-contact'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'emergency-contact-relationship'),
        30,
        FALSE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'medical-conditions'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'medical-conditions'),
        10,
        FALSE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'junior-policy'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'junior-declarations'),
        10,
        FALSE),

       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'social-basics'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'given-name'),
        10,
        TRUE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'social-basics'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'family-name'),
        20,
        TRUE),
       ((SELECT id
         FROM member_form_section
         WHERE KEY = 'social-basics'),
        (SELECT id
         FROM attribute_definition
         WHERE KEY = 'gender'),
        30,
        TRUE)
ON CONFLICT(
    member_form_section_id,
    attribute_definition_id
    ) DO UPDATE
    SET sort_order = EXCLUDED.sort_order,
        mandatory  = EXCLUDED.mandatory;

INSERT
INTO member_category_form_section(member_category_id,
                                  member_form_section_id,
                                  sort_order,
                                  show_on_registration)
VALUES ((SELECT id
         FROM member_category
         WHERE KEY = 'junior'),
        (SELECT id
         FROM member_form_section
         WHERE KEY = 'junior-basics'),
        10,
        TRUE),
       ((SELECT id
         FROM member_category
         WHERE KEY = 'junior'),
        (SELECT id
         FROM member_form_section
         WHERE KEY = 'parent'),
        20,
        TRUE),
       ((SELECT id
         FROM member_category
         WHERE KEY = 'junior'),
        (SELECT id
         FROM member_form_section
         WHERE KEY = 'medical-conditions'),
        30,
        TRUE),
       ((SELECT id
         FROM member_category
         WHERE KEY = 'junior'),
        (SELECT id
         FROM member_form_section
         WHERE KEY = 'junior-policy'),
        40,
        TRUE),
       ((SELECT id
         FROM member_category
         WHERE KEY = 'adult'),
        (SELECT id
         FROM member_form_section
         WHERE KEY = 'adult-basics'),
        10,
        TRUE),
       ((SELECT id
         FROM member_category
         WHERE KEY = 'adult'),
        (SELECT id
         FROM member_form_section
         WHERE KEY = 'contact'),
        20,
        TRUE),
       ((SELECT id
         FROM member_category
         WHERE KEY = 'adult'),
        (SELECT id
         FROM member_form_section
         WHERE KEY = 'emergency-contact'),
        30,
        TRUE),
       ((SELECT id
         FROM member_category
         WHERE KEY = 'disability'),
        (SELECT id
         FROM member_form_section
         WHERE KEY = 'disability-basics'),
        10,
        TRUE),
       ((SELECT id
         FROM member_category
         WHERE KEY = 'disability'),
        (SELECT id
         FROM member_form_section
         WHERE KEY = 'parent'),
        20,
        TRUE),
       ((SELECT id
         FROM member_category
         WHERE KEY = 'disability'),
        (SELECT id
         FROM member_form_section
         WHERE KEY = 'medical-conditions'),
        25,
        TRUE),
       ((SELECT id
         FROM member_category
         WHERE KEY = 'disability'),
        (SELECT id
         FROM member_form_section
         WHERE KEY = 'address'),
        30,
        TRUE),
       ((SELECT id
         FROM member_category
         WHERE KEY = 'social'),
        (SELECT id
         FROM member_form_section
         WHERE KEY = 'social-basics'),
        10,
        TRUE),
       ((SELECT id
         FROM member_category
         WHERE KEY = 'social'),
        (SELECT id
         FROM member_form_section
         WHERE KEY = 'contact'),
        20,
        TRUE),
       ((SELECT id
         FROM member_category
         WHERE KEY = 'honorary'),
        (SELECT id
         FROM member_form_section
         WHERE KEY = 'social-basics'),
        10,
        TRUE),
       ((SELECT id
         FROM member_category
         WHERE KEY = 'honorary'),
        (SELECT id
         FROM member_form_section
         WHERE KEY = 'contact'),
        20,
        TRUE)
ON CONFLICT(
    member_category_id,
    member_form_section_id
    ) DO UPDATE
    SET sort_order           = EXCLUDED.sort_order,
        show_on_registration = EXCLUDED.show_on_registration;

DELETE
FROM member_category_form_section
WHERE member_category_id = (SELECT id
                            FROM member_category
                            WHERE KEY = 'disability')
  AND member_form_section_id = (SELECT id
                                FROM member_form_section
                                WHERE KEY = 'junior-basics');

DELETE
FROM member_category_form_section
WHERE member_category_id = (SELECT id
                            FROM member_category
                            WHERE KEY = 'social')
  AND member_form_section_id = (SELECT id
                                FROM member_form_section
                                WHERE KEY = 'adult-basics');

DELETE
FROM member_category_form_section
WHERE member_category_id = (SELECT id
                            FROM member_category
                            WHERE KEY = 'honorary')
  AND member_form_section_id = (SELECT id
                                FROM member_form_section
                                WHERE KEY = 'adult-basics');

INSERT
INTO pricelist_item(id,
                    category_id,
                    min_age,
                    max_age,
                    description,
                    includes_match_fees,
                    students_only,
                    parent_discount,
                    inclusive_kit,
                    sort_order,
                    specific_gender)
VALUES (1,
        (SELECT id
         FROM member_category
         WHERE KEY = 'junior'),
        5,
        7,
        'Junior Girls & Boys (under 7)',
        NULL,
        FALSE,
        TRUE,
        FALSE,
        2,
        NULL),
       (2,
        (SELECT id
         FROM member_category
         WHERE KEY = 'junior'),
        8,
        18,
        'Juniors (U8 - U18)',
        NULL,
        FALSE,
        TRUE,
        TRUE,
        3,
        NULL),
       (3,
        (SELECT id
         FROM member_category
         WHERE KEY = 'adult'),
        19,
        NULL,
        'Students (over 18)',
        FALSE,
        TRUE,
        FALSE,
        FALSE,
        2,
        NULL),
       (4,
        (SELECT id
         FROM member_category
         WHERE KEY = 'adult'),
        19,
        NULL,
        'Students (all inclusive)',
        TRUE,
        TRUE,
        FALSE,
        FALSE,
        10,
        NULL),
       (5,
        (SELECT id
         FROM member_category
         WHERE KEY = 'adult'),
        18,
        NULL,
        'Adults (over 18)',
        FALSE,
        FALSE,
        FALSE,
        FALSE,
        3,
        NULL),
       (6,
        (SELECT id
         FROM member_category
         WHERE KEY = 'adult'),
        18,
        NULL,
        'Adults (all inclusive)',
        TRUE,
        FALSE,
        FALSE,
        FALSE,
        11,
        NULL),
       (7,
        (SELECT id
         FROM member_category
         WHERE KEY = 'adult'),
        18,
        NULL,
        'Women (over 18)',
        NULL,
        FALSE,
        FALSE,
        FALSE,
        1,
        'FEMALE'),
       (8,
        (SELECT id
         FROM member_category
         WHERE KEY = 'disability'),
        5,
        NULL,
        'Magics Memberships',
        NULL,
        FALSE,
        FALSE,
        FALSE,
        1,
        NULL),
       (9,
        (SELECT id
         FROM member_category
         WHERE KEY = 'social'),
        18,
        NULL,
        'Social',
        NULL,
        FALSE,
        FALSE,
        FALSE,
        1,
        NULL),
       (10,
        (SELECT id
         FROM member_category
         WHERE KEY = 'adult'),
        18,
        NULL,
        'Walking Cricket',
        NULL,
        FALSE,
        FALSE,
        FALSE,
        1,
        NULL),
       (11,
        (SELECT id
         FROM member_category
         WHERE KEY = 'junior'),
        8,
        18,
        'Girls (U8 - U18)',
        NULL,
        FALSE,
        TRUE,
        TRUE,
        1,
        'FEMALE'),
       (12,
        (SELECT id
         FROM member_category
         WHERE KEY = 'honorary'),
        18,
        NULL,
        'Honorary/Life Membership',
        NULL,
        FALSE,
        FALSE,
        FALSE,
        1,
        NULL)
ON CONFLICT(id) DO UPDATE
    SET category_id         = EXCLUDED.category_id,
        min_age             = EXCLUDED.min_age,
        max_age             = EXCLUDED.max_age,
        description         = EXCLUDED.description,
        includes_match_fees = EXCLUDED.includes_match_fees,
        students_only       = EXCLUDED.students_only,
        parent_discount     = EXCLUDED.parent_discount,
        inclusive_kit       = EXCLUDED.inclusive_kit,
        sort_order          = EXCLUDED.sort_order,
        specific_gender     = EXCLUDED.specific_gender;

DELETE FROM pricelist_item_info;

INSERT INTO pricelist_item_info (pricelist_item_id, key, icon, description)
VALUES (
            3,
            'payg',
            'fa-money-bill-wave',
        'Match fee due for each match played'
       ),
       (
           5,
           'payg',
           'fa-money-bill-wave',
           'Match fee due for each match played'
       ),
       (
           7,
           'payg',
           'fa-money-bill-wave',
           'Match fee due for each match played'
       ),
       (
           12,
           'invite-only',
           'fa-envelope-open',
           'For invited members only'
       );

INSERT
INTO pricelist(pricelist_item_id,
               date_from,
               date_to,
               price)
VALUES
       (1,      -- Juniors (U5 - U7)
        '2026-01-01',
        '2026-12-31',
        95.00),
       (2,      -- Juniors (U8 - U18)
        '2026-01-01',
        '2026-12-31',
        130.00),
       (3,      -- Students (over 18)
        '2026-01-01',
        '2026-12-31',
        115.00),
       (4,      -- Students (over 18) - All Inclusive
        '2026-01-01',
        '2026-12-31',
        315.00),
       (5,      -- Adult (Mens)
        '2026-01-01',
        '2026-12-31',
        160.00),
       (6,      -- Adult (Mens) - All Inclusive
        '2026-01-01',
        '2026-12-31',
        460.00),
       (7,      -- Women (over 18)
        '2026-01-01',
        '2026-12-31',
        100.00),
       (9,      -- Social
        '2026-01-01',
        '2026-12-31',
        45.00),
       (11,     -- U8 - U18 Girls
        '2026-01-01',
        '2026-12-31',
        100.00),
       (12,     -- Honorary/Life Membership
        '2025-01-01',
        '9999-12-31',
        0.00)
ON CONFLICT(
    pricelist_item_id,
    date_from,
    date_to
    ) DO UPDATE
    SET price = EXCLUDED.price;
