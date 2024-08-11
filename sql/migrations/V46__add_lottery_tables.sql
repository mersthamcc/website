CREATE TABLE lottery_ticket
(
    id            SERIAL,
    owner_user_id VARCHAR(64),
    holder_name   TEXT,
    purchase_date TIMESTAMP,
    expiry_date   TIMESTAMP,
    active        BOOLEAN,
    PRIMARY KEY (id)
);

CREATE TABLE lottery_payment
(
    id                 SERIAL,
    payment_method_id  BIGINT         NOT NULL,
    "date"             DATE           NOT NULL,
    payment_reference  VARCHAR(64),
    amount             DECIMAL(10, 2) NOT NULL,
    fees_amount        DECIMAL(10, 2) NOT NULL,
    accounting_id      VARCHAR(256),
    fees_accounting_id VARCHAR(256),
    status             TEXT           NOT NULL DEFAULT 'pending',
    reconciled         BOOLEAN                 DEFAULT FALSE,
    accounting_error   TEXT           NULL,
    PRIMARY KEY (id)
);

ALTER TABLE user_payment_methods
    ADD PRIMARY KEY (id);
ALTER TABLE lottery_payment
    ADD CONSTRAINT FK_LOTTERY_PAYMENTS_PAYMENT_METHOD_ID FOREIGN KEY (payment_method_id) REFERENCES user_payment_methods (id) NOT DEFERRABLE INITIALLY IMMEDIATE;

CREATE TABLE lottery_winner
(
    id               SERIAL,
    owner_user_id    VARCHAR(64),
    ticket_number_id BIGINT         NOT NULL,
    draw_date        DATE           NOT NULL,
    prize_fund       DECIMAL(10, 2) NOT NULL,
    prize_percent    INT            NOT NULL,
    prize_amount     DECIMAL(10, 2) NOT NULL,
    payout_date      DATE           NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE lottery_winner
    ADD CONSTRAINT FK_LOTTERY_WINNERS_TICKET_NUMBER_ID FOREIGN KEY (ticket_number_id) REFERENCES lottery_ticket (id) NOT DEFERRABLE INITIALLY IMMEDIATE;
