package com.chuckcha.currencyexchange.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Currency;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class CurrencyEntity {

    private final int id;
    private final Currency currency;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrencyEntity that)) return false;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
