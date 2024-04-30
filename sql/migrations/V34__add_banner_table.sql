CREATE TABLE message
(
    "key"      VARCHAR(32) NOT NULL,
    class      VARCHAR(64) NOT NULL,
    message    TEXT,
    enabled    BOOLEAN,
    start_date TIMESTAMP(0) WITHOUT TIME ZONE,
    end_date   TIMESTAMP(0) WITHOUT TIME ZONE,
    PRIMARY KEY ("key")
);
