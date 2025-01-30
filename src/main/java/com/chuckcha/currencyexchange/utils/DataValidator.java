package com.chuckcha.currencyexchange.utils;

import java.math.BigDecimal;
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

    public static boolean doValuesHaveNull(Object value1, Object value2, Object value3) {
        return value1 == null || value2 == null || value3 == null;
    }

    public static boolean doValuesHaveNull(Object ... values) {
        for (Object value : values) {
            if (value == null) return true;
        }
        return false;
    }

    public static boolean isRateInvalid (String baseCurrencyCode, String targetCurrencyCode) {
        try {
            Currency baseCurrency = Currency.getInstance(baseCurrencyCode);
            Currency targetCurrency = Currency.getInstance(targetCurrencyCode);
            return (baseCurrency == null || targetCurrency == null || baseCurrencyCode.equals(targetCurrencyCode));
        } catch (IllegalArgumentException e) {
            return true;
        }
    }


}
