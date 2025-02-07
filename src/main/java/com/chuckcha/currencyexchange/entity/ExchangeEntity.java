package com.chuckcha.currencyexchange.entity;

import java.math.BigDecimal;

public record ExchangeEntity(int id, CurrencyEntity baseCurrencyEntity, CurrencyEntity targetCurrencyEntity,
                             BigDecimal rate) {
}
