INSERT INTO currencies (code, full_name, sign)
VALUES ('USD', 'US Dollar', '$'),
       ('EUR', 'Euro', '€');

INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate)
VALUES (1,2,0.95);