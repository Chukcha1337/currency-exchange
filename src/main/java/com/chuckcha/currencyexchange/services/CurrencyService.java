package com.chuckcha.currencyexchange.services;

import java.util.List;

public interface CurrencyService<T> {

    List<T> findAll();

    T findByCode(String code);

    T insertNewValue(String value1, String value2, String value3);
}
