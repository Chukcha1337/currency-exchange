package com.chuckcha.currencyexchange.dto;

public record CurrencyDto(int id, String name, String code, String symbol) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrencyDto that)) return false;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
