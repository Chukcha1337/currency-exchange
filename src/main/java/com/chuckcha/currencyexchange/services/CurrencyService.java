package com.chuckcha.currencyexchange.services;

import com.chuckcha.currencyexchange.dao.CurrencyDao;
import com.chuckcha.currencyexchange.dto.CurrencyDto;
import com.chuckcha.currencyexchange.entity.CurrencyEntity;
import com.chuckcha.currencyexchange.exceptions.DataNotFoundException;

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
                .map(this::createCurrencyDto).toList();
    }

    public CurrencyDto findCurrencyByCode(String code) {
        Optional<CurrencyDto> currencyDto = currencyDao.findByCode(code).map(this::createCurrencyDto);
        if (currencyDto.isPresent()) {
            return currencyDto.get();
        } else {
            throw new DataNotFoundException("Currency with code " + code + " not found");
        }
    }

    public CurrencyDto insertNewCurrency(String code, String name, String sign) {
        Optional<CurrencyDto> currencyDto = currencyDao.insertNewCurrency(code,name,sign).map(this::createCurrencyDto);
        if (currencyDto.isPresent()) {
            return currencyDto.get();
        } else {
            throw new RuntimeException("Failed to insert new currency");
        }
    }

    private CurrencyDto createCurrencyDto(CurrencyEntity currencyEntity) {
        return new CurrencyDto(
                currencyEntity.getId(),
                currencyEntity.getCurrency().getDisplayName(),
                currencyEntity.getCurrency().getCurrencyCode(),
                currencyEntity.getCurrency().getSymbol()
        );
    }
}
