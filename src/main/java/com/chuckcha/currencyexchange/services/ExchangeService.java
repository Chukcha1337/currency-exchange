package com.chuckcha.currencyexchange.services;

import com.chuckcha.currencyexchange.dao.ExchangeDao;
import com.chuckcha.currencyexchange.dto.CurrencyDto;
import com.chuckcha.currencyexchange.dto.ExchangeDto;
import com.chuckcha.currencyexchange.entity.ExchangeEntity;
import com.chuckcha.currencyexchange.exceptions.DataAlreadyExistsException;
import com.chuckcha.currencyexchange.exceptions.DataNotExistsException;
import com.chuckcha.currencyexchange.exceptions.DataNotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ExchangeService {

    private static final ExchangeService INSTANCE = new ExchangeService();

    private final ExchangeDao exchangeDao = ExchangeDao.getInstance();

    private ExchangeService() {
    }

    public static ExchangeService getInstance() {
        return INSTANCE;
    }

    public List<ExchangeDto> findAll() {
        return exchangeDao.findAll().stream().map(this::createExchangeDto).toList();
    }

    public ExchangeDto findExchangeRateByCode(String baseCurrencyCode, String targetCurrencyCode) {
        Optional<ExchangeDto> exchangeDto = exchangeDao.findByCode(baseCurrencyCode, targetCurrencyCode).map(this::createExchangeDto);
        if (exchangeDto.isPresent()) {
            return exchangeDto.get();
        } else {
            throw new DataNotFoundException("Such currency pair exchange rate is not found");
        }
    }

    public ExchangeDto insertNewRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        Optional<ExchangeDto> exchangeDto = exchangeDao.insertNewExchangeRate(baseCurrencyCode, targetCurrencyCode, rate).map(this::createExchangeDto);
        if (exchangeDto.isPresent()) {
            return exchangeDto.get();
        } else {
            throw new RuntimeException("Failed to insert new exchange pair");
        }
    }

    public ExchangeDto updateExchangeRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws DataAlreadyExistsException, DataNotExistsException {
        Optional<ExchangeDto> exchangeDto = exchangeDao.updateExchangeRate(baseCurrencyCode, targetCurrencyCode, rate).map(this::createExchangeDto);
        if (exchangeDto.isPresent()) {
            return exchangeDto.get();
        } else {
            throw new DataNotExistsException("One or many currencies are not exist");
        }
    }

    private ExchangeDto createExchangeDto(ExchangeEntity exchangeEntity) {
        return new ExchangeDto(
                exchangeEntity.getId(),
                new CurrencyDto(
                        exchangeEntity.getBaseCurrencyEntity().getId(),
                        exchangeEntity.getBaseCurrencyEntity().getCurrency().getDisplayName(),
                        exchangeEntity.getBaseCurrencyEntity().getCurrency().getCurrencyCode(),
                        exchangeEntity.getBaseCurrencyEntity().getCurrency().getSymbol()
                ),
                new CurrencyDto(
                        exchangeEntity.getTargetCurrencyEntity().getId(),
                        exchangeEntity.getTargetCurrencyEntity().getCurrency().getDisplayName(),
                        exchangeEntity.getTargetCurrencyEntity().getCurrency().getCurrencyCode(),
                        exchangeEntity.getTargetCurrencyEntity().getCurrency().getSymbol()
                ),
                exchangeEntity.getRate());
    }
}
