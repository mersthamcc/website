ALTER TABLE "member"
    ADD COLUMN uuid VARCHAR(64) NOT NULL DEFAULT gen_random_uuid();
