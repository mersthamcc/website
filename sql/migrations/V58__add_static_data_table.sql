CREATE TABLE IF NOT EXISTS static_data (
    id SERIAL NOT NULL,
    path VARCHAR(256) NOT NULL,
    content_type VARCHAR(128),
    status_code INT,
    content TEXT,
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX idx_static_data_path ON static_data (path);
