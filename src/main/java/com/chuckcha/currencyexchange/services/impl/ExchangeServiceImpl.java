package com.chuckcha.currencyexchange.services.impl;

import com.chuckcha.currencyexchange.dao.ExchangeDao;
import com.chuckcha.currencyexchange.dto.ExchangeDto;
import com.chuckcha.currencyexchange.dto.ExchangeOperationDto;
import com.chuckcha.currencyexchange.entity.ExchangeEntity;
import com.chuckcha.currencyexchange.exceptions.DataNotFoundException;
import com.chuckcha.currencyexchange.exceptions.CurrencyExchangeAppRuntimeException;
import com.chuckcha.currencyexchange.mapper.DtoMapper;
import com.chuckcha.currencyexchange.services.ExchangeService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public final class ExchangeServiceImpl implements ExchangeService<ExchangeDto> {

    private static final int BASE_CURRENCY_CODE_FIRST_INDEX = 1;
    private static final int TARGET_CURRENCY_CODE_FIRST_INDEX = 4;
    private static final String PRINCIPAL_CURRENCY_CODE = "USD";
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
        String baseCurrencyCode = code.substring(BASE_CURRENCY_CODE_FIRST_INDEX, TARGET_CURRENCY_CODE_FIRST_INDEX);
        String targetCurrencyCode = code.substring(TARGET_CURRENCY_CODE_FIRST_INDEX);
        return exchangeDao.findByCode(baseCurrencyCode, targetCurrencyCode)
                .map(DtoMapper::toDto)
                .orElseThrow(() -> new DataNotFoundException("Such currency pair exchange rate is not found"));
    }

    public ExchangeDto insertNewValue(String baseCurrencyCode, String targetCurrencyCode, String stringRate) {
        BigDecimal rate = new BigDecimal(stringRate);
        return exchangeDao.create(baseCurrencyCode, targetCurrencyCode, rate)
                .map(DtoMapper::toDto)
                .orElseThrow(() -> new CurrencyExchangeAppRuntimeException("Failed to insert new exchange pair"));
    }

    public ExchangeDto updateExchangeRate(String code, String stringRate) {
        String baseCurrencyCode = code.substring(BASE_CURRENCY_CODE_FIRST_INDEX, TARGET_CURRENCY_CODE_FIRST_INDEX);
        String targetCurrencyCode = code.substring(TARGET_CURRENCY_CODE_FIRST_INDEX);
        BigDecimal rate = new BigDecimal(stringRate);
        return exchangeDao.updateExchangeRate(baseCurrencyCode, targetCurrencyCode, rate)
                .map(DtoMapper::toDto)
                .orElseThrow(() -> new DataNotFoundException("One or many currencies are not exist"));
    }

    public ExchangeOperationDto doExchangeOperation(String baseCurrencyCode, String targetCurrencyCode, String stringAmount) {
        Map<String, ExchangeEntity> rates = exchangeDao.getExchangeOptions(baseCurrencyCode, targetCurrencyCode);
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(stringAmount));
        if (!rates.isEmpty()) {
            ExchangeEntity straightRateEntity = rates.get(baseCurrencyCode + System.lineSeparator() + targetCurrencyCode);
            if (straightRateEntity != null) {
                BigDecimal actualRate = straightRateEntity.rate();
                BigDecimal convertedAmount = straightRateEntity.rate().multiply(amount);
                return DtoMapper.toDto(
                        DtoMapper.toDto(straightRateEntity.baseCurrencyEntity()),
                        DtoMapper.toDto(straightRateEntity.targetCurrencyEntity()),
                        actualRate,
                        amount,
                        convertedAmount);
            }
            ExchangeEntity reversedRateEntity = rates.get(targetCurrencyCode + System.lineSeparator() + baseCurrencyCode);
            if (reversedRateEntity != null) {
                BigDecimal actualRate = BigDecimal.ONE.divide(reversedRateEntity.rate(), 6, RoundingMode.DOWN);
                BigDecimal convertedAmount = actualRate.multiply(amount);
                return DtoMapper.toDto(
                        DtoMapper.toDto(reversedRateEntity.baseCurrencyEntity()),
                        DtoMapper.toDto(reversedRateEntity.targetCurrencyEntity()),
                        actualRate,
                        amount,
                        convertedAmount);
            }
            ExchangeEntity usdBaseRateEntity = rates.get(PRINCIPAL_CURRENCY_CODE + System.lineSeparator() + baseCurrencyCode);
            ExchangeEntity usdTargetRateEntity = rates.get(PRINCIPAL_CURRENCY_CODE + System.lineSeparator() + targetCurrencyCode);
            if (usdBaseRateEntity != null && usdTargetRateEntity != null) {
                BigDecimal actualRate = usdTargetRateEntity.rate().divide(usdBaseRateEntity.rate(), 6, RoundingMode.DOWN);
                BigDecimal convertedAmount = actualRate.multiply(amount);
                return DtoMapper.toDto(
                        DtoMapper.toDto(usdBaseRateEntity.targetCurrencyEntity()),
                        DtoMapper.toDto(usdTargetRateEntity.targetCurrencyEntity()),
                        actualRate,
                        amount,
                        convertedAmount);
            }
        }
        throw new DataNotFoundException("There is no option to convert " + baseCurrencyCode + " to " + targetCurrencyCode);
    }
}



