CREATE TABLE member
(
    id                SERIAL,
    given_name        VARCHAR(64),
    family_name       VARCHAR(64),
    gender            VARCHAR(1),
    dob               DATE,
    registration_date TIMESTAMP,
    PRIMARY KEY (id)
);