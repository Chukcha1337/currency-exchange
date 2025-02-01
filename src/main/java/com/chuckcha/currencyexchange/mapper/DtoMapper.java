package com.chuckcha.currencyexchange.mapper;

import com.chuckcha.currencyexchange.dto.CurrencyDto;
import com.chuckcha.currencyexchange.dto.ExchangeDto;
import com.chuckcha.currencyexchange.dto.ExchangeOperationDto;
import com.chuckcha.currencyexchange.entity.CurrencyEntity;
import com.chuckcha.currencyexchange.entity.ExchangeEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DtoMapper {

    private DtoMapper() {}

    public static CurrencyDto toDto(CurrencyEntity currencyEntity) {
        return new CurrencyDto(
                currencyEntity.getId(),
                currencyEntity.getCurrency().getDisplayName(),
                currencyEntity.getCurrency().getCurrencyCode(),
                currencyEntity.getCurrency().getSymbol()
        );
    }

    public static ExchangeDto toDto(ExchangeEntity exchangeEntity) {
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
                exchangeEntity.getRate().setScale(2, RoundingMode.HALF_DOWN).doubleValue());
    }

    public static ExchangeOperationDto toDto(CurrencyDto baseCurrency, CurrencyDto targetCurrency, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {
        return new ExchangeOperationDto(
                baseCurrency,
                targetCurrency,
                rate.setScale(2,RoundingMode.HALF_DOWN).doubleValue(),
                amount.setScale(2,RoundingMode.HALF_DOWN).doubleValue(),
                convertedAmount.setScale(2,RoundingMode.HALF_DOWN).doubleValue());
    }
}
