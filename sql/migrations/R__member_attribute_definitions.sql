INSERT
    INTO
        member_category(KEY)
    VALUES('adult'),
    ('junior'),
    ('disability'),
    ('social') ON
    CONFLICT DO NOTHING;

INSERT
    INTO
        attribute_definition(
            KEY,
            TYPE,
            choices
        )
    VALUES(
        'given-name',
        'String',
        NULL
    ),
    (
        'family-name',
        'String',
        NULL
    ),
    (
        'school',
        'String',
        NULL
    ),
    (
        'dob',
        'Date',
        NULL
    ),
    (
        'parent-name-1',
        'String',
        NULL
    ),
    (
        'parent-name-2',
        'String',
        NULL
    ),
    (
        'parent-number-1',
        'String',
        NULL
    ),
    (
        'parent-number-2',
        'String',
        NULL
    ),
    (
        'parent-email-1',
        'Email',
        NULL
    ),
    (
        'parent-email-2',
        'Email',
        NULL
    ),
    (
        'address-1',
        'String',
        NULL
    ),
    (
        'address-2',
        'String',
        NULL
    ),
    (
        'city',
        'String',
        NULL
    ),
    (
        'county',
        'String',
        NULL
    ),
    (
        'postal-code',
        'String',
        NULL
    ),
    (
        'email',
        'Email',
        NULL
    ),
    (
        'phone',
        'String',
        NULL
    ),
    (
        'gender',
        'Option',
        '[
         "MALE",
         "FEMALE",
         "NON-BINARY",
         "NOT-SPECIFIED"
       ]'::JSONB
    ),
    (
        'skills',
        'List',
        '[
         "BOWL",
         "BAT",
         "KEEPER"
       ]'::JSONB
    ),
    (
        'preferred-days',
        'List',
        '[
         "SATURDAY",
         "SUNDAY"
       ]'::JSONB
    ),
    (
        'open-age-allowed',
        'Boolean',
        NULL
    ) ON
    CONFLICT DO NOTHING;

INSERT
    INTO
        member_form_section(KEY)
    VALUES('contact'),
    ('parent'),
    ('address'),
    ('adult-cricket'),
    ('junior-cricket'),
    ('adult-basics'),
    ('junior-basics') ON
    CONFLICT DO NOTHING;

INSERT
    INTO
        member_form_section_attribute(
            member_form_section_id,
            attribute_definition_id,
            sort_order,
            mandatory
        )
    VALUES(
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'junior-basics'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'given-name'
        ),
        10,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'junior-basics'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'family-name'
        ),
        20,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'junior-basics'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'dob'
        ),
        30,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'junior-basics'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'gender'
        ),
        40,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'junior-basics'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'school'
        ),
        50,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'adult-basics'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'given-name'
        ),
        10,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'adult-basics'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'family-name'
        ),
        20,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'adult-basics'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'gender'
        ),
        30,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'contact'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'phone'
        ),
        10,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'contact'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'email'
        ),
        20,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'parent'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'parent-name-1'
        ),
        10,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'parent'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'parent-email-1'
        ),
        20,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'parent'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'parent-number-1'
        ),
        30,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'parent'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'parent-name-2'
        ),
        40,
        FALSE
    ),
    (
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'parent'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'parent-email-2'
        ),
        50,
        FALSE
    ),
    (
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'parent'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'parent-number-2'
        ),
        60,
        FALSE
    ),
    (
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'address'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'address-1'
        ),
        10,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'address'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'address-2'
        ),
        20,
        FALSE
    ),
    (
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'address'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'city'
        ),
        30,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'address'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'county'
        ),
        40,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'address'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'postal-code'
        ),
        50,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'adult-cricket'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'skills'
        ),
        10,
        FALSE
    ),
    (
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'adult-cricket'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'preferred-days'
        ),
        20,
        FALSE
    ),
    (
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'junior-cricket'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'skills'
        ),
        10,
        FALSE
    ),
    (
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'junior-cricket'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'preferred-days'
        ),
        20,
        FALSE
    ),
    (
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'junior-cricket'
        ),
        (
            SELECT
                id
            FROM
                attribute_definition
            WHERE
                KEY = 'open-age-allowed'
        ),
        30,
        FALSE
    ) ON
    CONFLICT(
        member_form_section_id,
        attribute_definition_id
    ) DO UPDATE
    SET
        sort_order = EXCLUDED.sort_order,
        mandatory = EXCLUDED.mandatory;

INSERT
    INTO
        member_category_form_section(
            member_category_id,
            member_form_section_id,
            sort_order,
            show_on_registration
        )
    VALUES(
        (
            SELECT
                id
            FROM
                member_category
            WHERE
                KEY = 'junior'
        ),
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'junior-basics'
        ),
        10,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_category
            WHERE
                KEY = 'junior'
        ),
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'parent'
        ),
        20,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_category
            WHERE
                KEY = 'junior'
        ),
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'junior-cricket'
        ),
        30,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_category
            WHERE
                KEY = 'adult'
        ),
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'adult-basics'
        ),
        10,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_category
            WHERE
                KEY = 'adult'
        ),
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'adult-cricket'
        ),
        20,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_category
            WHERE
                KEY = 'disability'
        ),
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'junior-basics'
        ),
        10,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_category
            WHERE
                KEY = 'disability'
        ),
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'parent'
        ),
        20,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_category
            WHERE
                KEY = 'disability'
        ),
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'address'
        ),
        30,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_category
            WHERE
                KEY = 'disability'
        ),
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'junior-cricket'
        ),
        40,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_category
            WHERE
                KEY = 'social'
        ),
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'adult-basics'
        ),
        10,
        TRUE
    ),
    (
        (
            SELECT
                id
            FROM
                member_category
            WHERE
                KEY = 'social'
        ),
        (
            SELECT
                id
            FROM
                member_form_section
            WHERE
                KEY = 'contact'
        ),
        20,
        TRUE
    ) ON
    CONFLICT DO NOTHING;

INSERT
    INTO
        pricelist_item(
            id,
            category_id,
            min_age,
            max_age,
            description,
            includes_match_fees
        )
    VALUES(
        1,
        (
            SELECT
                id
            FROM
                member_category
            WHERE
                KEY = 'junior'
        ),
        5,
        7,
        'Juniors (under 7)',
        NULL
    ),
    (
        2,
        (
            SELECT
                id
            FROM
                member_category
            WHERE
                KEY = 'junior'
        ),
        8,
        15,
        'Juniors (U8 - U15)',
        NULL
    ),
    (
        3,
        (
            SELECT
                id
            FROM
                member_category
            WHERE
                KEY = 'adult'
        ),
        16,
        NULL,
        'Students (U16 and older)',
        FALSE
    ),
    (
        4,
        (
            SELECT
                id
            FROM
                member_category
            WHERE
                KEY = 'adult'
        ),
        16,
        NULL,
        'Students (inclusive of match fees)',
        FALSE
    ),
    (
        5,
        (
            SELECT
                id
            FROM
                member_category
            WHERE
                KEY = 'adult'
        ),
        16,
        NULL,
        'Adults',
        FALSE
    ),
    (
        6,
        (
            SELECT
                id
            FROM
                member_category
            WHERE
                KEY = 'adult'
        ),
        16,
        NULL,
        'Adults (inclusive of match fees)',
        TRUE
    ),
    (
        7,
        (
            SELECT
                id
            FROM
                member_category
            WHERE
                KEY = 'adult'
        ),
        15,
        NULL,
        'Ladies (15 years and older)',
        NULL
    ),
    (
        8,
        (
            SELECT
                id
            FROM
                member_category
            WHERE
                KEY = 'disability'
        ),
        5,
        NULL,
        'Disability (All Ages)',
        NULL
    ),
    (
        9,
        (
            SELECT
                id
            FROM
                member_category
            WHERE
                KEY = 'social'
        ),
        16,
        NULL,
        'Social',
        NULL
    ) ON
    CONFLICT DO NOTHING;

INSERT
    INTO
        pricelist(
            pricelist_item_id,
            date_from,
            date_to,
            price
        )
    VALUES(
        1,
        '2021-01-01',
        '2021-12-31',
        40.00
    ),
    (
        2,
        '2021-01-01',
        '2021-12-31',
        70.00
    ),
    (
        3,
        '2021-01-01',
        '2021-05-31',
        50.00
    ),
    (
        3,
        '2021-06-01',
        '2021-12-31',
        60.00
    ),
    (
        4,
        '2021-01-01',
        '2021-05-31',
        155.00
    ),
    (
        5,
        '2021-01-01',
        '2021-05-31',
        90.00
    ),
    (
        5,
        '2021-06-01',
        '2021-12-31',
        110.00
    ),
    (
        6,
        '2021-01-01',
        '2021-05-31',
        300.00
    ),
    (
        7,
        '2021-01-01',
        '2021-12-31',
        40.00
    ),
    (
        8,
        '2021-01-01',
        '2021-12-31',
        40.00
    ),
    (
        9,
        '2021-01-01',
        '2021-12-31',
        25.00
    ),
    (
        1,
        '2024-01-01',
        '2024-12-31',
        75.00
    ),
    (
        2,
        '2024-01-01',
        '2024-12-31',
        100.00
    ),
    (
        3,
        '2024-01-01',
        '2024-03-31',
        90.00
    ),
    (
        3,
        '2024-04-01',
        '2024-12-31',
        100.00
    ),
    (
        4,
        '2024-01-01',
        '2024-03-31',
        250.00
    ),
    (
        5,
        '2024-01-01',
        '2024-03-31',
        130.00
    ),
    (
        5,
        '2024-04-01',
        '2024-12-31',
        150.00
    ),
    (
        6,
        '2024-01-01',
        '2024-03-31',
        450.00
    ),
    (
        7,
        '2024-01-01',
        '2024-12-31',
        75.00
    ),
    (
        8,
        '2024-01-01',
        '2024-12-31',
        75.00
    ),
    (
        9,
        '2024-01-01',
        '2024-12-31',
        40.00
    ) ON
    CONFLICT DO NOTHING;
