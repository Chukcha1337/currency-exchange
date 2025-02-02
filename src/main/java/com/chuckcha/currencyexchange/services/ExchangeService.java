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

public class ExchangeService {

    private static final ExchangeService INSTANCE = new ExchangeService();

    private final ExchangeDao exchangeDao = ExchangeDao.getInstance();

    private ExchangeService() {
    }

    public static ExchangeService getInstance() {
        return INSTANCE;
    }

    public List<ExchangeDto> findAll() {
        return exchangeDao.findAll().stream().map(DtoMapper::toDto).toList();
    }

    public ExchangeDto findExchangeRateByCode(String baseCurrencyCode, String targetCurrencyCode) {
        Optional<ExchangeDto> exchangeDto = exchangeDao.findByCode(baseCurrencyCode, targetCurrencyCode).map(DtoMapper::toDto);
        if (exchangeDto.isPresent()) {
            return exchangeDto.get();
        } else {
            throw new DataNotFoundException("Such currency pair exchange rate is not found");
        }
    }

    public ExchangeDto insertNewRate(String baseCurrencyCode, String targetCurrencyCode, String stringRate) {
        BigDecimal rate = BigDecimal.valueOf(Double.parseDouble(stringRate));
        Optional<ExchangeDto> exchangeDto = exchangeDao.insertNewExchangeRate(baseCurrencyCode, targetCurrencyCode, rate).map(DtoMapper::toDto);
        if (exchangeDto.isPresent()) {
            return exchangeDto.get();
        } else {
            throw new RuntimeException("Failed to insert new exchange pair");
        }
    }

    public ExchangeDto updateExchangeRate(String baseCurrencyCode, String targetCurrencyCode, String stringRate) {
        BigDecimal rate = BigDecimal.valueOf(Double.parseDouble(stringRate));
        Optional<ExchangeDto> exchangeDto = exchangeDao.updateExchangeRate(baseCurrencyCode, targetCurrencyCode, rate).map(DtoMapper::toDto);
        if (exchangeDto.isPresent()) {
            return exchangeDto.get();
        } else {
            throw new DataNotExistsException("One or many currencies are not exist");
        }
    }

    public ExchangeOperationDto doExchangeOperation(String baseCurrencyCode, String targetCurrencyCode, String stringAmount) {
        CurrencyDto baseCurrency = CurrencyService.getInstance().findCurrencyByCode(baseCurrencyCode);
        CurrencyDto targetCurrency = CurrencyService.getInstance().findCurrencyByCode(targetCurrencyCode);
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
