package com.chuckcha.currencyexchange.dto;

import java.math.BigDecimal;
import java.util.Objects;

public record ExchangeOperationDto(CurrencyDto baseCurrency, CurrencyDto targetCurrency, Double rate, Double amount, Double convertedAmount) {

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ExchangeOperationDto that)) return false;

        return Objects.equals(rate, that.rate) && Objects.equals(amount, that.amount) && Objects.equals(baseCurrency, that.baseCurrency) && Objects.equals(targetCurrency, that.targetCurrency) && Objects.equals(convertedAmount, that.convertedAmount);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(baseCurrency);
        result = 31 * result + Objects.hashCode(targetCurrency);
        result = 31 * result + Objects.hashCode(rate);
        result = 31 * result + Objects.hashCode(amount);
        result = 31 * result + Objects.hashCode(convertedAmount);
        return result;
    }
}
