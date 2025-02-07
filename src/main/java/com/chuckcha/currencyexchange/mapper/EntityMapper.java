package com.chuckcha.currencyexchange.mapper;

import com.chuckcha.currencyexchange.entity.CurrencyEntity;
import com.chuckcha.currencyexchange.entity.ExchangeEntity;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;

@UtilityClass
public class EntityMapper {

    public static CurrencyEntity buildCurrencyEntity(ResultSet resultSet) throws SQLException {
        return new CurrencyEntity(
                resultSet.getObject("id", Integer.class),
                Currency.getInstance(resultSet.getString("code"))
        );
    }

    public static ExchangeEntity buildExchangeEntity(ResultSet resultSet) throws SQLException {
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
