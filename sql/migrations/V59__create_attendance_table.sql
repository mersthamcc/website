CREATE TABLE member_attendance (
    id SERIAL,
    member_id INT NOT NULL,
    date TIMESTAMP NOT NULL,
    reference VARCHAR(64) NOT NULL,
    event VARCHAR(64) NOT NULL,
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX idx_member_attendance_reference ON member_attendance(reference);
ALTER TABLE member_attendance
    ADD CONSTRAINT FK_MEMBER_ATTENDANCE_MEMBER_ID FOREIGN KEY(member_id)
        REFERENCES member(id) NOT DEFERRABLE INITIALLY IMMEDIATE;