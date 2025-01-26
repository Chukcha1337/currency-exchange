package com.chuckcha.currencyexchange.dao;

import com.chuckcha.currencyexchange.entity.Currency;
import com.chuckcha.currencyexchange.utils.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao implements Dao<Long, Currency> {

    private static final CurrencyDao INSTANCE = new CurrencyDao();

    private static final String FIND_ALL = """
                        SELECT *
                        FROM currencies
            """;
    private CurrencyDao() {}

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }

    @Override
    public List<Currency> findAll() {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Currency> currencies = new ArrayList<>();
            while (resultSet.next()) {
                currencies.add(buildFlight(resultSet));
            }

            return currencies;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private Currency buildFlight(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getObject("id", Integer.class),
                resultSet.getObject("code", String.class),
                resultSet.getObject("full_name", String.class),
                resultSet.getObject("sign", String.class)
        );
    }

    @Override
    public Optional<Currency> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    @Override
    public void update(Currency entity) {

    }

    @Override
    public Currency save(Currency entity) {
        return null;
    }
}
