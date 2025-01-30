package com.chuckcha.currencyexchange.services;

import com.chuckcha.currencyexchange.dao.ExchangeDao;
import com.chuckcha.currencyexchange.dto.CurrencyDto;
import com.chuckcha.currencyexchange.dto.ExchangeDto;
import com.chuckcha.currencyexchange.exceptions.DataAlreadyExistsException;
import com.chuckcha.currencyexchange.exceptions.DataNotExistsException;

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
        return exchangeDao.findAll().stream()
                .map(exchange -> new ExchangeDto(
                        exchange.getId(),
                        new CurrencyDto(
                                exchange.getBaseCurrencyEntity().getId(),
                                exchange.getBaseCurrencyEntity().getCurrency().getDisplayName(),
                                exchange.getBaseCurrencyEntity().getCurrency().getCurrencyCode(),
                                exchange.getBaseCurrencyEntity().getCurrency().getSymbol()
                        ),
                        new CurrencyDto(
                                exchange.getTargetCurrencyEntity().getId(),
                                exchange.getTargetCurrencyEntity().getCurrency().getDisplayName(),
                                exchange.getTargetCurrencyEntity().getCurrency().getCurrencyCode(),
                                exchange.getTargetCurrencyEntity().getCurrency().getSymbol()
                        ),
                        exchange.getRate()
                ))
                .toList();
    }

    public Optional<ExchangeDto> findExchangeRateByCode(String baseCurrencyCode, String targetCurrencyCode) {
        return exchangeDao.findByCode(baseCurrencyCode, targetCurrencyCode)
                .map(exchange -> new ExchangeDto(
                        exchange.getId(),
                        new CurrencyDto(
                                exchange.getBaseCurrencyEntity().getId(),
                                exchange.getBaseCurrencyEntity().getCurrency().getDisplayName(),
                                exchange.getBaseCurrencyEntity().getCurrency().getCurrencyCode(),
                                exchange.getBaseCurrencyEntity().getCurrency().getSymbol()
                        ),
                        new CurrencyDto(
                                exchange.getTargetCurrencyEntity().getId(),
                                exchange.getTargetCurrencyEntity().getCurrency().getDisplayName(),
                                exchange.getTargetCurrencyEntity().getCurrency().getCurrencyCode(),
                                exchange.getTargetCurrencyEntity().getCurrency().getSymbol()
                        ),
                        exchange.getRate()
                ));
    }

    public Optional<ExchangeDto> insertNewRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws DataAlreadyExistsException, DataNotExistsException {
        return exchangeDao.insertNewExchangeRate(baseCurrencyCode,targetCurrencyCode,rate)
                .map(exchange -> new ExchangeDto(
                        exchange.getId(),
                        new CurrencyDto(
                                exchange.getBaseCurrencyEntity().getId(),
                                exchange.getBaseCurrencyEntity().getCurrency().getDisplayName(),
                                exchange.getBaseCurrencyEntity().getCurrency().getCurrencyCode(),
                                exchange.getBaseCurrencyEntity().getCurrency().getSymbol()
                        ),
                        new CurrencyDto(
                                exchange.getTargetCurrencyEntity().getId(),
                                exchange.getTargetCurrencyEntity().getCurrency().getDisplayName(),
                                exchange.getTargetCurrencyEntity().getCurrency().getCurrencyCode(),
                                exchange.getTargetCurrencyEntity().getCurrency().getSymbol()
                        ),
                        exchange.getRate()
                ));
    }

    public Optional<ExchangeDto> updateExchangeRate (String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws DataAlreadyExistsException, DataNotExistsException {
        return exchangeDao.updateExchangeRate(baseCurrencyCode,targetCurrencyCode,rate)
                .map(exchange -> new ExchangeDto(
                        exchange.getId(),
                        new CurrencyDto(
                                exchange.getBaseCurrencyEntity().getId(),
                                exchange.getBaseCurrencyEntity().getCurrency().getDisplayName(),
                                exchange.getBaseCurrencyEntity().getCurrency().getCurrencyCode(),
                                exchange.getBaseCurrencyEntity().getCurrency().getSymbol()
                        ),
                        new CurrencyDto(
                                exchange.getTargetCurrencyEntity().getId(),
                                exchange.getTargetCurrencyEntity().getCurrency().getDisplayName(),
                                exchange.getTargetCurrencyEntity().getCurrency().getCurrencyCode(),
                                exchange.getTargetCurrencyEntity().getCurrency().getSymbol()
                        ),
                        exchange.getRate()
                ));
    }


}
