package com.chuckcha.currencyexchange.services;

import com.chuckcha.currencyexchange.dao.CurrencyDao;
import com.chuckcha.currencyexchange.dto.CurrencyDto;
import com.chuckcha.currencyexchange.exceptions.DataNotFoundException;
import com.chuckcha.currencyexchange.mapper.DtoMapper;

import java.util.List;

public class CurrencyServiceImpl implements Service<CurrencyDto> {

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
                .orElseThrow(() -> new RuntimeException("Failed to insert new currency"));
    }
}
