package com.chuckcha.currencyexchange.dao;

import java.util.*;

import com.chuckcha.currencyexchange.entity.CurrencyEntity;
import com.chuckcha.currencyexchange.utils.DatabaseConfig;

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
