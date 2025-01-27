package com.chuckcha.currencyexchange.exeptions;

public class ErrorException extends RuntimeException {

    public ErrorException(String message) {
        super(message);
    }
}
