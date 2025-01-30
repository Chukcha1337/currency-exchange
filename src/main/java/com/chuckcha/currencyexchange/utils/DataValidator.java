package com.chuckcha.currencyexchange.utils;

import com.chuckcha.currencyexchange.exceptions.InvalidValueException;
import com.chuckcha.currencyexchange.exceptions.NullInsertException;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.util.Currency;

public class DataValidator {

    private DataValidator() {
    }

    public static void validateCurrency(String name, String code, String sign) {
        if (doValuesHaveNull(name, code, sign)) {
            throw new NullInsertException("One or many values are null");
        } else if (isCurrencyInvalid(name, code, sign)) {
            throw new InvalidValueException("One or many values are invalid");
        }
    }

    public static void validatePath(String path) {
        if (path == null || path.equals("/")) {
            throw new InvalidValueException("There's no currency code at this URL");
        }
    }

    public static void validateExchangeRate(String baseCurrencyCode,String targetCurrencyCode, BigDecimal rate) {
        if (doValuesHaveNull(baseCurrencyCode, targetCurrencyCode, rate)) {
            throw new NullInsertException("One or many values are null");
        } else if (isRateInvalid(baseCurrencyCode, targetCurrencyCode)) {
            throw new InvalidValueException("One or many values are invalid");
        }
    }

    public static void validateExchangeRate(BigDecimal rate) {
        if (doValuesHaveNull(rate)) {
            throw new NullInsertException("Rate is null");
        }
    }

    public static void validateBody(String body) {
        if (doValuesHaveNull(body)) {
            throw new NullInsertException("Body is null");
        } else if (!body.contains("rate")) {
            throw new NullInsertException("There is no rate at body");
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

    private static boolean doValuesHaveNull(Object... values) {
        for (Object value : values) {
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
