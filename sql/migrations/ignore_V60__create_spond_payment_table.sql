CREATE TABLE IF NOT EXISTS payment (
   id SERIAL,
   created TIMESTAMP NOT NULL,
   order_number TEXT NOT NULL,
   category TEXT NULL,
   product TEXT NOT NULL,
   price DECIMAL(5,2) NOT NULL,
   fees DECIMAL(5,2) NULL,
   "type" TEXT NOT NULL,
   sage_id TEXT NULL,
   ledger_account TEXT NULL,
   fees_sage_id TEXT NULL,
   PRIMARY KEY (id)
);
