package com.chuckcha.currencyexchange.dto;

public record ExchangeDto(int id, CurrencyDto baseCurrency, CurrencyDto targetCurrency, Double rate) {
}
