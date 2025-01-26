package com.chuckcha.currencyexchange.dto;

import java.util.Objects;

public class CurrencyDto {
    final private int id;
    final private String description;

    public CurrencyDto(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrencyDto that)) return false;

        return Objects.equals(id, that.id) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(description);
        return result;
    }

    @Override
    public String toString() {
        return "CurrencyDto{" +
               "id=" + id +
               ", description='" + description + '\'' +
               '}';
    }
}
