package com.chuckcha.currencyexchange.dao;

import com.chuckcha.currencyexchange.entity.ExchangeEntity;
import com.chuckcha.currencyexchange.mapper.EntityMapper;

import java.math.BigDecimal;
import java.util.*;

public class ExchangeDao extends AbstractDao {

    private static final ExchangeDao INSTANCE = new ExchangeDao();

    private static final String FIND_ALL = """
            SELECT exchange_rates.id id,
                   exchange_rates.rate rate,
                   base_currency.id bc_id,
                   base_currency.code bc_code,
                   target_currency.id tc_id,
                   target_currency.code tc_code
            FROM exchange_rates
            JOIN currencies base_currency on exchange_rates.base_currency_id = base_currency.id
            JOIN currencies target_currency on exchange_rates.target_currency_id = target_currency.id
            """;

    private static final String FIND_BY_CODE = FIND_ALL + """
            WHERE base_currency.code = ? AND target_currency.code = ?;
            """;

    private static final String INSERT_NEW_RATE = """
            INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate)
            VALUES ((SELECT id FROM currencies WHERE code = ?), (SELECT id FROM currencies WHERE code = ?), ?)
            """;

    private static final String UPDATE_RATE = """
            UPDATE exchange_rates
            SET rate = ?
            WHERE base_currency_id = (SELECT id FROM currencies WHERE code = ?)
            AND target_currency_id = (SELECT id FROM currencies WHERE code = ?)
            """;

    private static final String GET_SELECTED_RATES = FIND_ALL + """
            WHERE (base_currency.code = ? AND target_currency.code = ?)
             OR (base_currency.code = ? AND target_currency.code = ?)
             OR (base_currency.code = ? AND target_currency.code = ?)
             OR (base_currency.code = ? AND target_currency.code = ?);
            """;


    private ExchangeDao() {
    }

    public static ExchangeDao getInstance() {
        return INSTANCE;
    }

    public List<ExchangeEntity> findAll() {
        return executeQuery(FIND_ALL,
                preparedStatement -> {
                },
                resultSet -> {
                    List<ExchangeEntity> exchangeRates = new LinkedList<>();
                    while (resultSet.next()) {
                        exchangeRates.add(EntityMapper.buildExchangeEntity(resultSet));
                    }
                    return exchangeRates;
                });
    }


    public Optional<ExchangeEntity> findByCode(String baseCurrencyCode, String targetCurrencyCode) {
        return executeQuery(
                FIND_BY_CODE,
                preparedStatement -> {
                    preparedStatement.setObject(1, baseCurrencyCode);
                    preparedStatement.setObject(2, targetCurrencyCode);
                },
                resultSet -> resultSet.next() ? Optional.of(EntityMapper.buildExchangeEntity(resultSet)) : Optional.empty());
    }

    public Optional<ExchangeEntity> create(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        executeUpdate(
                INSERT_NEW_RATE,
                preparedStatement -> {
                    preparedStatement.setObject(1, baseCurrencyCode);
                    preparedStatement.setObject(2, targetCurrencyCode);
                    preparedStatement.setObject(3, rate);
                });
        return findByCode(baseCurrencyCode, targetCurrencyCode);

    }

    public Optional<ExchangeEntity> updateExchangeRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        executeUpdate(
                UPDATE_RATE,
                preparedStatement -> {
                    preparedStatement.setObject(1, rate);
                    preparedStatement.setObject(2, baseCurrencyCode);
                    preparedStatement.setObject(3, targetCurrencyCode);
                });
        return findByCode(baseCurrencyCode, targetCurrencyCode);

    }

    public Map<String, ExchangeEntity> getExchangeOptions(String baseCurrencyCode, String targetCurrencyCode) {
        return executeQuery(
                GET_SELECTED_RATES,
                preparedStatement -> {
                    preparedStatement.setString(1, baseCurrencyCode);
                    preparedStatement.setString(2, targetCurrencyCode);
                    preparedStatement.setString(3, targetCurrencyCode);
                    preparedStatement.setString(4, baseCurrencyCode);
                    preparedStatement.setString(5, "USD");
                    preparedStatement.setString(6, baseCurrencyCode);
                    preparedStatement.setString(7, "USD");
                    preparedStatement.setString(8, targetCurrencyCode);
                },
                resultSet -> {
                    Map<String, ExchangeEntity> rates = new HashMap<>();
                    while (resultSet.next()) {
                        String baseCode = resultSet.getString("bc_code");
                        String targetCode = resultSet.getString("tc_code");
                        rates.put(baseCode + System.lineSeparator() + targetCode, EntityMapper.buildExchangeEntity(resultSet));
                    }
                    return rates;
                });
    }
}
