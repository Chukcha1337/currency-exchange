package com.chuckcha.currencyexchange.dao;

import java.util.*;

import com.chuckcha.currencyexchange.entity.CurrencyEntity;
import com.chuckcha.currencyexchange.mapper.EntityMapper;

public final class CurrencyDao extends AbstractDao {

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
        return executeQuery(FIND_ALL,
                preparedStatement -> {
                },
                resultSet -> {
                    List<CurrencyEntity> currencies = new LinkedList<>();
                    while (resultSet.next()) {
                        currencies.add(EntityMapper.buildCurrencyEntity(resultSet));
                    }
                    return currencies;
                });
    }

    public Optional<CurrencyEntity> findByCode(String code) {
        return executeQuery(FIND_BY_CODE,
                parameterSetter -> parameterSetter.setObject(1, code),
                resultSet -> resultSet.next() ? Optional.of(EntityMapper.buildCurrencyEntity(resultSet)) : Optional.empty());
    }

    public Optional<CurrencyEntity> create(String code, String name, String sign) {
        return executeQuery(INSERT_NEW_CURRENCY,
                preparedStatement -> {
                    preparedStatement.setObject(1, code);
                    preparedStatement.setObject(2, name);
                    preparedStatement.setObject(3, sign);
                },
                resultSet -> resultSet.next() ? Optional.of(EntityMapper.buildCurrencyEntity(resultSet)) : Optional.empty());
    }
}

