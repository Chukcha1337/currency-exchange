package com.chuckcha.currencyexchange.utils;

import com.chuckcha.currencyexchange.exceptions.NullInsertException;

import java.util.Currency;

public class DataValidator {

    private DataValidator() {
    }

    public static boolean isCurrencyValid(String code, String name, String sign) {
        if (code == null || name == null || sign == null) {
            return false;
        } else {
            try {
            Currency currency = Currency.getInstance(code);
            return currency != null && currency.getDisplayName().equals(name) && currency.getSymbol().equals(sign);
        } catch (IllegalArgumentException e) {
                return false;
            }
        }
    }
}
