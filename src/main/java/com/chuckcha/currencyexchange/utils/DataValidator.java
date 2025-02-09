package com.chuckcha.currencyexchange.utils;

import java.util.Currency;

public final class DataValidator {

    private DataValidator() {
    }

    public static void validateCurrency(String name, String code, String sign) {
        if (doValuesHaveNull(name, code, sign)) {
            throw new IllegalArgumentException("One or many values are null");
        } else if (isCurrencyInvalid(name, code, sign)) {
            throw new IllegalArgumentException("One or many values are invalid");
        }
    }

    public static void validatePath(String path) {
        if (path == null || path.equals("/")) {
            throw new IllegalArgumentException("There's no currency code at this URL");
        }
    }

    public static void validateExchangeRate(String baseCurrencyCode,String targetCurrencyCode, String rate) {
        if (doValuesHaveNull(baseCurrencyCode, targetCurrencyCode, rate)) {
            throw new IllegalArgumentException("One or many values are null");
        } else if (isRateInvalid(baseCurrencyCode, targetCurrencyCode) || baseCurrencyCode.equals(targetCurrencyCode)) {
            throw new IllegalArgumentException("One or many values are invalid");
        }
    }

    public static void validateExchangeRate(String rate) {
        if (doValuesHaveNull(rate)) {
            throw new IllegalArgumentException("Rate is null");
        } else if (Double.parseDouble(rate) < 0) {
            throw new IllegalArgumentException("One or many values are invalid");
        }
    }

    public static void validateBody(String body) {
        if (doValuesHaveNull(body)) {
            throw new IllegalArgumentException("Body is null");
        } else if (!body.contains("rate")) {
            throw new IllegalArgumentException("There is no rate at body");
        }
    }

    private static boolean isCurrencyInvalid(String name, String code, String sign) {
        try {
            Currency currency = Currency.getInstance(code);
            return currency == null || !currency.getDisplayName().equals(name) || !currency.getSymbol().equals(sign);
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    private static boolean doValuesHaveNull(String... values) {
        for (String value : values) {
            if (value == null) return true;
        }
        return false;
    }

    private static boolean isRateInvalid(String baseCurrencyCode, String targetCurrencyCode) {
        try {
            Currency baseCurrency = Currency.getInstance(baseCurrencyCode);
            Currency targetCurrency = Currency.getInstance(targetCurrencyCode);
            return (baseCurrency == null || targetCurrency == null || baseCurrencyCode.equals(targetCurrencyCode));
        } catch (IllegalArgumentException e) {
            return true;
        }
    }
}
