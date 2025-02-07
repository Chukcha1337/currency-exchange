package com.chuckcha.currencyexchange.mapper;

import com.chuckcha.currencyexchange.dto.CurrencyDto;
import com.chuckcha.currencyexchange.dto.ExchangeDto;
import com.chuckcha.currencyexchange.dto.ExchangeOperationDto;
import com.chuckcha.currencyexchange.entity.CurrencyEntity;
import com.chuckcha.currencyexchange.entity.ExchangeEntity;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class DtoMapper {

    public static CurrencyDto toDto(CurrencyEntity currencyEntity) {
        return new CurrencyDto(
                currencyEntity.id(),
                currencyEntity.currency().getDisplayName(),
                currencyEntity.currency().getCurrencyCode(),
                currencyEntity.currency().getSymbol()
        );
    }

    public static ExchangeDto toDto(ExchangeEntity exchangeEntity) {
        return new ExchangeDto(
                exchangeEntity.id(),
                new CurrencyDto(
                        exchangeEntity.baseCurrencyEntity().id(),
                        exchangeEntity.baseCurrencyEntity().currency().getDisplayName(),
                        exchangeEntity.baseCurrencyEntity().currency().getCurrencyCode(),
                        exchangeEntity.baseCurrencyEntity().currency().getSymbol()
                ),
                new CurrencyDto(
                        exchangeEntity.targetCurrencyEntity().id(),
                        exchangeEntity.targetCurrencyEntity().currency().getDisplayName(),
                        exchangeEntity.targetCurrencyEntity().currency().getCurrencyCode(),
                        exchangeEntity.targetCurrencyEntity().currency().getSymbol()
                ),
                exchangeEntity.rate().setScale(2, RoundingMode.HALF_DOWN).doubleValue());
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
