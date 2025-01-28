package com.chuckcha.currencyexchange.exceptions;

import java.sql.SQLException;

public class NullInsertException extends RuntimeException {
    public NullInsertException(String message) {
        super(message);
    }
}
