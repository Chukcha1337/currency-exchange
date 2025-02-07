CREATE TABLE IF NOT EXISTS currencies
(
    id        SERIAL PRIMARY KEY,
    code      VARCHAR(3)  NOT NULL UNIQUE,
    full_name VARCHAR(64) NOT NULL,
    sign      VARCHAR(3)  NOT NULL
);
CREATE TABLE IF NOT EXISTS exchange_rates
(
    id                 SERIAL PRIMARY KEY,
    base_currency_id   INTEGER REFERENCES currencies (id) NOT NULL,
    target_currency_id INTEGER REFERENCES currencies (id) NOT NULL,
    rate               NUMERIC(10, 6)                     NOT NULL
);
CREATE UNIQUE INDEX unique_base_currency_id_target_currency_id_idx ON exchange_rates (base_currency_id, target_currency_id);