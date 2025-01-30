package com.chuckcha.currencyexchange.dao;

import com.chuckcha.currencyexchange.entity.CurrencyEntity;
import com.chuckcha.currencyexchange.entity.ExchangeEntity;
import com.chuckcha.currencyexchange.exceptions.DataAlreadyExistsException;
import com.chuckcha.currencyexchange.exceptions.DataNotExistsException;
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
            WHERE base_currency_id = (SELECT id FROM currencies WHERE code = ?) AND target_currency_id = (SELECT id FROM currencies WHERE code = ?)
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
                exchangeRates.add(buildExchangeEntity(resultSet));
            }
            return exchangeRates;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<ExchangeEntity> findByCode(String baseCurrencyCode, String targetCurrencyCode) {
        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CODE);
        ) {
            preparedStatement.setObject(1, baseCurrencyCode);
            preparedStatement.setObject(2, targetCurrencyCode);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next() ? Optional.of(buildExchangeEntity(resultSet)) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<ExchangeEntity> insertNewExchangeRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws DataAlreadyExistsException, DataNotExistsException {
        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement preparedStatementInsert = connection.prepareStatement(INSERT_NEW_RATE);
             PreparedStatement preparedStatementFind = connection.prepareStatement(FIND_BY_CODE);
        ) {
            preparedStatementInsert.setObject(1, baseCurrencyCode);
            preparedStatementInsert.setObject(2, targetCurrencyCode);
            preparedStatementInsert.setObject(3, rate);
            preparedStatementFind.setObject(1, baseCurrencyCode);
            preparedStatementFind.setObject(2, targetCurrencyCode);

            preparedStatementInsert.executeUpdate();
            ResultSet resultSet = preparedStatementFind.executeQuery();

            if (resultSet.next()) {
                return Optional.of(buildExchangeEntity(resultSet));
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                throw new DataAlreadyExistsException(baseCurrencyCode + " - " + targetCurrencyCode + " - " + rate);
            } else if (e.getSQLState().equals("23502")) {
                throw new DataNotExistsException("One or many currencies are not exist at database");
            }
        }
        return Optional.empty();
    }

    public Optional<ExchangeEntity> updateExchangeRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement preparedStatementUpdate = connection.prepareStatement(UPDATE_RATE);
             PreparedStatement preparedStatementFind = connection.prepareStatement(FIND_BY_CODE);
        ) {
            preparedStatementUpdate.setObject(1, rate);
            preparedStatementUpdate.setObject(2, baseCurrencyCode);
            preparedStatementUpdate.setObject(3, targetCurrencyCode);
            preparedStatementFind.setObject(1, baseCurrencyCode);
            preparedStatementFind.setObject(2, targetCurrencyCode);

            preparedStatementUpdate.executeUpdate();
            ResultSet resultSet = preparedStatementFind.executeQuery();

            if (resultSet.next()) {
                return Optional.of(buildExchangeEntity(resultSet));
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                throw new DataAlreadyExistsException(baseCurrencyCode + " - " + targetCurrencyCode + " - " + rate);
            } else if (e.getSQLState().equals("23502")) {
                throw new DataNotExistsException("One or many currencies are not exist at database");
            }
        }
        return Optional.empty();
    }

    private ExchangeEntity buildExchangeEntity(ResultSet resultSet) throws SQLException {
        return new ExchangeEntity(
                resultSet.getObject("id", Integer.class),
                new CurrencyEntity(
                        resultSet.getObject("bc_id", Integer.class),
                        Currency.getInstance(resultSet.getObject("bc_code", String.class))
                ),
                new CurrencyEntity(
                        resultSet.getObject("tc_id", Integer.class),
                        Currency.getInstance(resultSet.getObject("tc_code", String.class))
                ),
                resultSet.getObject("rate", BigDecimal.class)
        );
    }

}
