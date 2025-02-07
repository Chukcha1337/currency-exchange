package com.chuckcha.currencyexchange.dao;

import com.chuckcha.currencyexchange.entity.ExchangeEntity;
import com.chuckcha.currencyexchange.exceptions.DataAlreadyExistsException;
import com.chuckcha.currencyexchange.exceptions.DataNotExistsException;
import com.chuckcha.currencyexchange.mapper.EntityMapper;
import com.chuckcha.currencyexchange.utils.DatabaseConfig;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class ExchangeDao {

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

    private static final String GET_RATE = """
            SELECT rate
            FROM exchange_rates
            WHERE base_currency_id = (SELECT id FROM currencies WHERE code = ?)
            AND target_currency_id = (SELECT id FROM currencies WHERE code = ?);
            """;

    private ExchangeDao() {
    }

    public static ExchangeDao getInstance() {
        return INSTANCE;
    }

    public List<ExchangeEntity> findAll() {
        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            List<ExchangeEntity> exchangeRates = new LinkedList<>();
            while (resultSet.next()) {
                exchangeRates.add(EntityMapper.buildExchangeEntity(resultSet));
            }
            return exchangeRates;
        } catch (SQLException e) {
            throw new RuntimeException("Database error");
        }
    }

    public Optional<ExchangeEntity> findByCode(String baseCurrencyCode, String targetCurrencyCode) {
        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CODE)
        ) {
            preparedStatement.setObject(1, baseCurrencyCode);
            preparedStatement.setObject(2, targetCurrencyCode);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next() ? Optional.of(EntityMapper.buildExchangeEntity(resultSet)) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Database error");
        }
    }

    public Optional<ExchangeEntity> create(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        return executeExchangeRateQuery(
                INSERT_NEW_RATE,
                1,
                baseCurrencyCode,
                2,
                targetCurrencyCode,
                3,
                rate);
    }

    public Optional<ExchangeEntity> updateExchangeRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        return executeExchangeRateQuery(
                UPDATE_RATE,
                2,
                baseCurrencyCode,
                3,
                targetCurrencyCode,
                1,
                rate);
    }

    private Optional<ExchangeEntity> executeExchangeRateQuery(
            String sqlRequest,
            int baseCurrencyIndex,
            String baseCurrencyCode,
            int targetCurrencyIndex,
            String targetCurrencyCode,
            int rateIndex,
            BigDecimal rate) {
        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlRequest);
             PreparedStatement preparedStatementFind = connection.prepareStatement(FIND_BY_CODE)
        ) {
            preparedStatement.setObject(baseCurrencyIndex, baseCurrencyCode);
            preparedStatement.setObject(targetCurrencyIndex, targetCurrencyCode);
            preparedStatement.setObject(rateIndex, rate);
            preparedStatementFind.setObject(1, baseCurrencyCode);
            preparedStatementFind.setObject(2, targetCurrencyCode);

            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatementFind.executeQuery();

            if (resultSet.next()) {
                return Optional.of(EntityMapper.buildExchangeEntity(resultSet));
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                throw new DataAlreadyExistsException("Such currency pair exchange rate already exists");
            } else if (e.getSQLState().equals("23502")) {
                throw new DataNotExistsException("One or many currencies are not exist at database");
            }
        }
        return Optional.empty();
    }

    public Optional<BigDecimal> getRate(String baseCurrencyCode, String targetCurrencyCode) {
        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_RATE)) {

            preparedStatement.setObject(1, baseCurrencyCode);
            preparedStatement.setObject(2, targetCurrencyCode);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next() ? Optional.of(resultSet.getObject("rate", BigDecimal.class)) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Database error");
        }
    }
}
