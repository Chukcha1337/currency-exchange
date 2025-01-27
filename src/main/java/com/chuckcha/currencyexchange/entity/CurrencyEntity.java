package com.chuckcha.currencyexchange.entity;

import java.util.Currency;

public class CurrencyEntity {

    private final int id;
    private final Currency currency;

    public CurrencyEntity(int id, Currency currency) {
        this.id = id;
        this.currency = currency;
    }

    public int getId() {
        return id;
    }

    public Currency getCurrency() {
        return currency;
    }

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

    @Override
    public String toString() {
        return "CurrencyEntity{" +
               "id=" + id +
               ", currency=" + currency +
               '}';
    }
}
