package com.chuckcha.currencyexchange.exceptions;

public class InternalServerException extends RuntimeException {

    public InternalServerException(String message) {
        super(message);
    }
}