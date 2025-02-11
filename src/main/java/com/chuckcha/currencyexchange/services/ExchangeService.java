package com.chuckcha.currencyexchange.services;

import com.chuckcha.currencyexchange.dto.ExchangeOperationDto;

public interface ExchangeService<T> extends CurrencyService<T> {

    T updateExchangeRate(String value1, String value2);

    ExchangeOperationDto doExchangeOperation(String value1, String value2, String value3);
}
