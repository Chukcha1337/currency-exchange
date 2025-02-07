package com.chuckcha.currencyexchange.services;

import com.chuckcha.currencyexchange.dao.ExchangeDao;
import com.chuckcha.currencyexchange.dto.CurrencyDto;
import com.chuckcha.currencyexchange.dto.ExchangeDto;
import com.chuckcha.currencyexchange.dto.ExchangeOperationDto;
import com.chuckcha.currencyexchange.exceptions.DataNotExistsException;
import com.chuckcha.currencyexchange.exceptions.DataNotFoundException;
import com.chuckcha.currencyexchange.mapper.DtoMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

public class ExchangeServiceImpl implements ExchangeService<ExchangeDto> {

    private static final int baseCurrencyCodeFirstIndex = 1;
    private static final int targetCurrencyCodeFirstIndex = 4;
    private static final ExchangeServiceImpl INSTANCE = new ExchangeServiceImpl();

    private final ExchangeDao exchangeDao = ExchangeDao.getInstance();

    private ExchangeServiceImpl() {
    }

    public static ExchangeServiceImpl getInstance() {
        return INSTANCE;
    }

    public List<ExchangeDto> findAll() {
        return exchangeDao.findAll().stream().map(DtoMapper::toDto).toList();
    }

    public ExchangeDto findByCode(String code) {
        String baseCurrencyCode = code.substring(baseCurrencyCodeFirstIndex, targetCurrencyCodeFirstIndex);
        String targetCurrencyCode = code.substring(targetCurrencyCodeFirstIndex);
        return exchangeDao.findByCode(baseCurrencyCode, targetCurrencyCode)
                .map(DtoMapper::toDto)
                .orElseThrow(() -> new DataNotFoundException("Such currency pair exchange rate is not found"));
    }

    public ExchangeDto insertNewValue(String baseCurrencyCode, String targetCurrencyCode, String stringRate) {
        BigDecimal rate = BigDecimal.valueOf(Double.parseDouble(stringRate));
        return exchangeDao.create(baseCurrencyCode, targetCurrencyCode, rate)
                .map(DtoMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Failed to insert new exchange pair"));
    }

    public ExchangeDto updateExchangeRate(String code, String stringRate) {
        String baseCurrencyCode = code.substring(baseCurrencyCodeFirstIndex, targetCurrencyCodeFirstIndex);
        String targetCurrencyCode = code.substring(targetCurrencyCodeFirstIndex);
        BigDecimal rate = BigDecimal.valueOf(Double.parseDouble(stringRate));
        return exchangeDao.updateExchangeRate(baseCurrencyCode, targetCurrencyCode, rate)
                .map(DtoMapper::toDto)
                .orElseThrow(() -> new DataNotFoundException("One or many currencies are not exist"));
    }

    public ExchangeOperationDto doExchangeOperation(String baseCurrencyCode, String targetCurrencyCode, String stringAmount) {
        CurrencyDto baseCurrency = CurrencyServiceImpl.getInstance().findByCode(baseCurrencyCode);
        CurrencyDto targetCurrency = CurrencyServiceImpl.getInstance().findByCode(targetCurrencyCode);
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(stringAmount));

        Optional<BigDecimal> straightRate = exchangeDao.getRate(baseCurrencyCode, targetCurrencyCode);
        if (straightRate.isPresent()) {
            BigDecimal convertedAmount = straightRate.get().multiply(amount);
            return DtoMapper.toDto(baseCurrency, targetCurrency, straightRate.get(), amount, convertedAmount);
        } else {
            Optional<BigDecimal> reversedRate = exchangeDao.getRate(targetCurrencyCode, baseCurrencyCode);
            if (reversedRate.isPresent()) {
                BigDecimal actualRate = BigDecimal.ONE.divide(reversedRate.get(), 6, RoundingMode.DOWN);
                BigDecimal convertedAmount = actualRate.multiply(amount);
                return DtoMapper.toDto(baseCurrency, targetCurrency, actualRate, amount, convertedAmount);
            } else {
                Optional<BigDecimal> usdBaseCurrencyRate = exchangeDao.getRate("USD", baseCurrencyCode);
                Optional<BigDecimal> usdTargetCurrencyRate = exchangeDao.getRate("USD", targetCurrencyCode);
                if (usdBaseCurrencyRate.isPresent() && usdTargetCurrencyRate.isPresent()) {
                    BigDecimal actualRate = usdTargetCurrencyRate.get().divide(usdBaseCurrencyRate.get(), 6, RoundingMode.DOWN);
                    BigDecimal convertedAmount = actualRate.multiply(amount);
                    return DtoMapper.toDto(baseCurrency, targetCurrency, actualRate, amount, convertedAmount);
                }
            }
        }
        throw new DataNotExistsException("There is no option to convert " + baseCurrencyCode + " to " + targetCurrencyCode);
    }
}
