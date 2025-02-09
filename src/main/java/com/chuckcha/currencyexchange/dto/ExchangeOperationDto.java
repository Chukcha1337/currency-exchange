package com.chuckcha.currencyexchange.dto;

public record ExchangeOperationDto(CurrencyDto baseCurrency, CurrencyDto targetCurrency, Double rate, Double amount,
                                   Double convertedAmount) {
}
