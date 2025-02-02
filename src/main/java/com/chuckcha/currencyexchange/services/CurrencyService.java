package com.chuckcha.currencyexchange.services;

import com.chuckcha.currencyexchange.dao.CurrencyDao;
import com.chuckcha.currencyexchange.dto.CurrencyDto;
import com.chuckcha.currencyexchange.exceptions.DataNotFoundException;
import com.chuckcha.currencyexchange.mapper.DtoMapper;

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
                .map(DtoMapper::toDto).toList();
    }

    public CurrencyDto findCurrencyByCode(String code) {
        Optional<CurrencyDto> currencyDto = currencyDao.findByCode(code).map(DtoMapper::toDto);
        if (currencyDto.isPresent()) {
            return currencyDto.get();
        } else {
            throw new DataNotFoundException("Currency with code " + code + " not found");
        }
    }

    public CurrencyDto insertNewCurrency(String code, String name, String sign) {
        Optional<CurrencyDto> currencyDto = currencyDao.insertNewCurrency(code,name,sign).map(DtoMapper::toDto);
        if (currencyDto.isPresent()) {
            return currencyDto.get();
        } else {
            throw new RuntimeException("Failed to insert new currency");
        }
    }
}
