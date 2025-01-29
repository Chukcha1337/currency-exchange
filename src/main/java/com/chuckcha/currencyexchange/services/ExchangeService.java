package com.chuckcha.currencyexchange.services;

import com.chuckcha.currencyexchange.dao.ExchangeDao;

public class ExchangeService {

    private static final ExchangeService INSTANCE = new ExchangeService();
    private final ExchangeDao exchangeDao = ExchangeDao.getInstance();

    private ExchangeService() {}

    public static ExchangeService getInstance() {
        return INSTANCE;
    }

}
