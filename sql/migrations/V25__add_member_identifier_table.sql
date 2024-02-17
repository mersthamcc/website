CREATE
    TABLE
        member_identifier(
            member_id BIGINT NOT NULL,
            name VARCHAR(64) NOT NULL,
            value TEXT,
            PRIMARY KEY(
                member_id,
                name
            )
        );

CREATE
    INDEX IDX_MEMBER_IDENTIFIER_MEMBER_ID ON
    member_identifier(member_id);

ALTER TABLE
    member_identifier ADD CONSTRAINT FK_MEMBER_IDENTIFIER_MEMBER_ID FOREIGN KEY(member_id) REFERENCES MEMBER(id) NOT DEFERRABLE INITIALLY IMMEDIATE;
