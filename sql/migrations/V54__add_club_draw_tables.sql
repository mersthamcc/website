CREATE TABLE club_draw_subscription
(
    id              SERIAL,
    owner_user_id   VARCHAR(64),
    subscription_id TEXT,
    create_date     TIMESTAMP,
    last_updated    TIMESTAMP,
    active          BOOLEAN,
    PRIMARY KEY (id)
);

CREATE TABLE club_draw
(
    id         SERIAL,
    draw_date  DATE           NOT NULL,
    prize_fund DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE club_draw_payment
(
    id                        SERIAL,
    club_draw_subscription_id BIGINT         NOT NULL,
    "date"                    DATE           NOT NULL,
    payment_reference         VARCHAR(64),
    amount                    DECIMAL(10, 2) NOT NULL,
    fees_amount               DECIMAL(10, 2) NOT NULL,
    accounting_id             VARCHAR(256),
    fees_accounting_id        VARCHAR(256),
    status                    TEXT           NOT NULL DEFAULT 'pending',
    reconciled                BOOLEAN                 DEFAULT FALSE,
    accounting_error          TEXT           NULL,
    include_in_club_draw_id   BIGINT,
    PRIMARY KEY (id)
);

ALTER TABLE club_draw_payment
    ADD CONSTRAINT FK_CLUB_DRAW_PAYMENTS_CLUB_DRAW_SUBSCRIPTION_ID FOREIGN KEY (club_draw_subscription_id) REFERENCES club_draw_subscription (id) NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE club_draw_payment
    ADD CONSTRAINT FK_CLUB_DRAW_PAYMENTS_INCLUDE_IN_CLUB_DRAW_ID FOREIGN KEY (include_in_club_draw_id) REFERENCES club_draw (id) NOT DEFERRABLE INITIALLY IMMEDIATE;

CREATE TABLE club_draw_winner
(
    id                   SERIAL,
    club_draw_id         BIGINT         NOT NULL,
    club_draw_payment_id BIGINT         NOT NULL,
    prize_percent        INT            NOT NULL,
    prize_amount         DECIMAL(10, 2) NOT NULL,
    payout_date          DATE           NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE club_draw_winner
    ADD CONSTRAINT FK_CLUB_DRAW_WINNERS_CLUB_DRAW_ID FOREIGN KEY (club_draw_id) REFERENCES club_draw (id) NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE club_draw_winner
    ADD CONSTRAINT FK_CLUB_DRAW_WINNERS_CLUB_DRAW_PAYMENT_ID FOREIGN KEY (club_draw_payment_id) REFERENCES club_draw_subscription (id) NOT DEFERRABLE INITIALLY IMMEDIATE;
