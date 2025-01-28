package com.chuckcha.currencyexchange.dao;

import java.util.*;

import com.chuckcha.currencyexchange.entity.CurrencyEntity;
import com.chuckcha.currencyexchange.exceptions.CurrencyAlreadyExistsException;
import com.chuckcha.currencyexchange.exceptions.NullInsertException;
import com.chuckcha.currencyexchange.utils.DatabaseConfig;
import org.postgresql.util.PSQLException;

import java.sql.*;

public class CurrencyDao implements Dao<String, CurrencyEntity> {

    private static final CurrencyDao INSTANCE = new CurrencyDao();

    private static final String FIND_ALL = """
            SELECT *
            FROM currencies
            """;

    private static final String FIND_BY_CODE = """
            SELECT *
            FROM currencies
            WHERE code = ?
            """;

    private static final String INSERT_NEW_CURRENCY = """
            INSERT INTO currencies (code, full_name, sign)
            VALUES (?, ?, ?)
            RETURNING id, code, full_name, sign
            """;

    private CurrencyDao() {
    }

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }

    @Override
    public List<CurrencyEntity> findAll() {
        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            List<CurrencyEntity> currencies = new LinkedList<>();
            while (resultSet.next()) {
                currencies.add(buildCurrencyEntity(resultSet));
            }
            return currencies;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CurrencyEntity> findByCode(String code) {
        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CODE);
        ) {
            preparedStatement.setObject(1, code);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next() ? Optional.of(buildCurrencyEntity(resultSet)) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<CurrencyEntity> insertNewCurrency(String code, String name, String sign) throws SQLException {
        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_NEW_CURRENCY);
             ) {
            preparedStatement.setObject(1, code);
            preparedStatement.setObject(2, name);
            preparedStatement.setObject(3, sign);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(buildCurrencyEntity(resultSet));
            } else {
                return Optional.empty();
            }
        }
    }

    private CurrencyEntity buildCurrencyEntity(ResultSet resultSet) throws SQLException {
        return new CurrencyEntity(
                resultSet.getObject("id", Integer.class),
                Currency.getInstance(resultSet.getString("code"))
        );
    }

    @Override
    public boolean delete(CurrencyEntity entity) {
        return false;
    }

    @Override
    public void update(CurrencyEntity entity) {
    }

    @Override
    public CurrencyEntity save(CurrencyEntity entity) {
        return null;
    }
}
