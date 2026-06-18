CREATE TABLE member_attendance (
    id TEXT NOT NULL,
    time TIMESTAMP NOT NULL,
    member_id BIGINT NULL,
    non_member_name TEXT,
    event TEXT,
    PRIMARY KEY (id)
);

CREATE
    INDEX MEMBER_ATTENDANCE_MEMBER_ID ON
    member_attendance(member_id);

ALTER TABLE member_attendance
        ADD CONSTRAINT FK_MEMBER_ATTENDANCE_MEMBER_ID FOREIGN KEY(member_id) REFERENCES member(id)
            ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE;
