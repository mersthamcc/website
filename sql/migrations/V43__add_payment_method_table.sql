CREATE TABLE user_payment_methods
(
    id                  SERIAL,
    user_id             VARCHAR(64) NOT NULL,
    provider            VARCHAR(64) NOT NULL,
    type                VARCHAR(64) NOT NULL,
    customer_identifier TEXT,
    method_identifier   TEXT,
    create_date         TIMESTAMP   NOT NULL,
    status              TEXT
);

CREATE INDEX idx_user_payment_methods_user_id ON user_payment_methods (user_id);