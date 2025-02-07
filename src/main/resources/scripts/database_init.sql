CREATE SCHEMA IF NOT EXISTS currency_exchange;

CREATE TABLE IF NOT EXISTS currency_exchange.currencies
(
    id        SERIAL PRIMARY KEY,
    code      VARCHAR(3)  NOT NULL UNIQUE,
    full_name VARCHAR(64) NOT NULL,
    sign      VARCHAR(3)  NOT NULL
);
CREATE TABLE IF NOT EXISTS currency_exchange.exchange_rates
(
    id                 SERIAL PRIMARY KEY,
    base_currency_id   INTEGER REFERENCES currency_exchange.currencies (id) NOT NULL,
    target_currency_id INTEGER REFERENCES currency_exchange.currencies (id) NOT NULL,
    rate               NUMERIC(10, 6)                     NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS unique_base_currency_id_target_currency_id_idx ON currency_exchange.exchange_rates (base_currency_id, target_currency_id);