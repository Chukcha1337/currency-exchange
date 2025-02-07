package com.chuckcha.currencyexchange.dao;

import java.util.*;

import com.chuckcha.currencyexchange.entity.CurrencyEntity;
import com.chuckcha.currencyexchange.exceptions.DataAlreadyExistsException;
import com.chuckcha.currencyexchange.mapper.EntityMapper;
import com.chuckcha.currencyexchange.utils.DatabaseConfig;

import java.sql.*;

public class CurrencyDao {

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

    public List<CurrencyEntity> findAll() {
        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            List<CurrencyEntity> currencies = new LinkedList<>();
            while (resultSet.next()) {
                currencies.add(EntityMapper.buildCurrencyEntity(resultSet));
            }
            return currencies;
        } catch (SQLException e) {
            throw new RuntimeException("Database error");
        }
    }

    public Optional<CurrencyEntity> findByCode(String code) {
        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CODE)
        ) {
            preparedStatement.setObject(1, code);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next() ? Optional.of(EntityMapper.buildCurrencyEntity(resultSet)) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Database error");
        }
    }

    public Optional<CurrencyEntity> create(String code, String name, String sign) {
        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_NEW_CURRENCY)
        ) {
            preparedStatement.setObject(1, code);
            preparedStatement.setObject(2, name);
            preparedStatement.setObject(3, sign);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(EntityMapper.buildCurrencyEntity(resultSet));
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                throw new DataAlreadyExistsException("Currency with code " + code + " already exists");
            }
        }
        return Optional.empty();
    }
}
