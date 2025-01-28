package com.chuckcha.currencyexchange.utils;

import com.chuckcha.currencyexchange.exceptions.NullInsertException;

import java.util.Currency;

public class DataValidator {

    private DataValidator() {
    }

    public static boolean isCurrencyValid(String code, String name, String sign) throws NullInsertException, IllegalArgumentException {
        if (code == null || name == null || sign == null) {
            throw new NullInsertException("There is no required filed(s)");
        } else {
            Currency currency = Currency.getInstance(code);
            return currency != null && currency.getDisplayName().equals(name) && currency.getSymbol().equals(sign);
        }
    }
}
