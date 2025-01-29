package com.chuckcha.currencyexchange.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class ExchangeEntity {

    private final int id;
    private final CurrencyEntity baseCurrencyEntity;
    private final CurrencyEntity targetCurrencyEntity;
    private final BigDecimal rate;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrencyEntity that)) return false;

        return id == that.getId();
    }

    @Override
    public int hashCode() {
        return id;
    }

}
