CREATE TABLE live_stream (
    id SERIAL,
    title VARCHAR(128) NOT NULL,
    description TEXT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NULL,
    fixture_id BIGINT NULL,
    frogbox_id VARCHAR(128) NOT NULL,
    youtube_id VARCHAR(128) NOT NULL,
    signage_id VARCHAR(128) NULL,
    signage_schedule_id VARCHAR(128) NULL,
    thumbnail_url VARCHAR(512) NULL,
    widget TEXT,
    PRIMARY KEY (id)
);

CREATE INDEX IDX_LIVE_STREAM_FIXTURE_ID ON live_stream(fixture_id);

CREATE UNIQUE INDEX IDX_LIVE_STREAM_YOUTUBE_ID ON live_stream(youtube_id);

ALTER TABLE
    live_stream ADD CONSTRAINT FK_LIVE_STREAM_FIXTURE_ID FOREIGN KEY(fixture_id) REFERENCES fixture(id);
