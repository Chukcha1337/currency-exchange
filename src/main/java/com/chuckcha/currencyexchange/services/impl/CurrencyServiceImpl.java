package com.chuckcha.currencyexchange.services.impl;

import com.chuckcha.currencyexchange.dao.CurrencyDao;
import com.chuckcha.currencyexchange.dto.CurrencyDto;
import com.chuckcha.currencyexchange.exceptions.DataNotFoundException;
import com.chuckcha.currencyexchange.exceptions.CurrencyExchangeAppRuntimeException;
import com.chuckcha.currencyexchange.mapper.DtoMapper;
import com.chuckcha.currencyexchange.services.CurrencyService;

import java.util.List;

public final class CurrencyServiceImpl implements CurrencyService<CurrencyDto> {

    private static final CurrencyServiceImpl INSTANCE = new CurrencyServiceImpl();

    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private CurrencyServiceImpl() {}

    public static CurrencyServiceImpl getInstance() {
        return INSTANCE;
    }

    public List<CurrencyDto> findAll() {
        return currencyDao.findAll()
                .stream()
                .map(DtoMapper::toDto)
                .toList();
    }

    public CurrencyDto findByCode(String code) {
        return currencyDao.findByCode(code)
                .map(DtoMapper::toDto)
                .orElseThrow(() -> new DataNotFoundException("Currency with code " + code + " not found"));

    }

    public CurrencyDto insertNewValue(String code, String name, String sign) {
        return currencyDao.create(code,name,sign)
                .map(DtoMapper::toDto)
                .orElseThrow(() -> new CurrencyExchangeAppRuntimeException("Failed to insert new currency"));
    }
}
