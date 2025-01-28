package com.chuckcha.currencyexchange.services;

import com.chuckcha.currencyexchange.dao.CurrencyDao;
import com.chuckcha.currencyexchange.dto.CurrencyDto;
import com.chuckcha.currencyexchange.exceptions.CurrencyAlreadyExistsException;
import com.chuckcha.currencyexchange.exceptions.NullInsertException;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CurrencyService {

    private static final CurrencyService INSTANCE = new CurrencyService();

    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private CurrencyService() {}

    public static CurrencyService getInstance() {
        return INSTANCE;
    }

    public List<CurrencyDto> findAll() {
        return currencyDao.findAll().stream()
                .map(currency -> new CurrencyDto(
                        currency.getId(),
                        currency.getCurrency().getDisplayName(),
                        currency.getCurrency().getCurrencyCode(),
                        currency.getCurrency().getSymbol()
                        ))
                .toList();
    }

    public Optional<CurrencyDto> findCurrencyByCode(String code) {
        return currencyDao.findByCode(code).map(currency -> new CurrencyDto(
                currency.getId(),
                currency.getCurrency().getDisplayName(),
                currency.getCurrency().getCurrencyCode(),
                currency.getCurrency().getSymbol()
        ));
    }

    public Optional<CurrencyDto> insertNewCurrency(String code, String name, String sign) throws CurrencyAlreadyExistsException {
        return currencyDao.insertNewCurrency(code,name,sign).map(currency -> new CurrencyDto(
                currency.getId(),
                currency.getCurrency().getDisplayName(),
                currency.getCurrency().getCurrencyCode(),
                currency.getCurrency().getSymbol()
        ));
    }
}
