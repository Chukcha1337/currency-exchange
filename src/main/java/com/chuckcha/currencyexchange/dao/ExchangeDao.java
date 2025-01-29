package com.chuckcha.currencyexchange.dao;

public class ExchangeDao {

    private static final ExchangeDao INSTANCE = new ExchangeDao();

    private ExchangeDao() {}

    public static ExchangeDao getInstance() {
        return INSTANCE;
    }
}
