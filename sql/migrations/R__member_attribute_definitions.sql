INSERT INTO member_category
(
    key
)
VALUES
('adult'),
('junior'),
('disability'),
('social')
ON CONFLICT DO NOTHING ;

INSERT INTO attribute_definition(key, type, encrypt, choices)
VALUES
('school', 'String', FALSE, NULL)
;