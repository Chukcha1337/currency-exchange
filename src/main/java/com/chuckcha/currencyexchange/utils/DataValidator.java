package com.chuckcha.currencyexchange.utils;

import java.util.Currency;

public class DataValidator {

    private DataValidator() {
    }

    public static boolean isCurrencyInvalid(String code, String name, String sign) {
        try {
            Currency currency = Currency.getInstance(code);
            return currency == null || !currency.getDisplayName().equals(name) || !currency.getSymbol().equals(sign);
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    public static boolean doValuesHaveNull(String code, String name, String sign) {
        return code == null || name == null || sign == null;
    }
}
