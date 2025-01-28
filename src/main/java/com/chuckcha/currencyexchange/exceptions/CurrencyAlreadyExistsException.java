package com.chuckcha.currencyexchange.exceptions;

import java.sql.SQLException;

public class CurrencyAlreadyExistsException extends RuntimeException {

    public CurrencyAlreadyExistsException(String message) {
        super(message);
    }
}
