package com.chuckcha.currencyexchange.dto;

import java.math.BigDecimal;

public record ExchangeDto(int id, CurrencyDto baseCurrency, CurrencyDto targetCurrency, Double rate) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExchangeDto that)) return false;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
