CREATE TABLE passkit_device_registration (
    id SERIAL NOT NULL,
    device_library_identifier VARCHAR(256) NOT NULL UNIQUE,
    push_token TEXT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE passkit_device_member_link (
    device_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    PRIMARY KEY (device_id, member_id)
);

ALTER TABLE passkit_device_member_link
    ADD CONSTRAINT FK_PASSKIT_DEVICE_MEMBER_LINK_DEVICE_ID FOREIGN KEY(device_id)
        REFERENCES passkit_device_registration(id) NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE passkit_device_member_link
    ADD CONSTRAINT FK_PASSKIT_DEVICE_MEMBER_LINK_MEMBER_ID FOREIGN KEY(member_id)
        REFERENCES member(id) NOT DEFERRABLE INITIALLY IMMEDIATE;
