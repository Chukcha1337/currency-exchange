package com.chuckcha.currencyexchange.utils;

import com.chuckcha.currencyexchange.dto.ErrorResponseDto;
import com.chuckcha.currencyexchange.exceptions.DataAlreadyExistsException;
import com.chuckcha.currencyexchange.exceptions.DataNotFoundException;
import com.chuckcha.currencyexchange.exceptions.InternalServerException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

public final class ExceptionHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String NOT_NULL_VIOLATION = "23502";
    private static final String UNIQUE_VIOLATION = "23505";
    private static final String STRING_DATA_RIGHT_TRUNCATION = "22001";
    private static final String NUMERIC_VALUE_OUT_OF_RANGE = "22003";

    public static void handleExceptions(HttpServletResponse resp, Throwable throwable) throws IOException {
        resp.setStatus(getStatusCode(throwable));
        resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponseDto(throwable.getMessage())));
    }

    private static int getStatusCode(Throwable throwable) {
        return switch (throwable.getClass().getSimpleName()) {
            case "IllegalArgumentException" -> HttpServletResponse.SC_BAD_REQUEST;
            case "DataAlreadyExistsException" -> HttpServletResponse.SC_CONFLICT;
            case "DataNotFoundException" -> HttpServletResponse.SC_NOT_FOUND;
            case "InternalServerException" -> HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            default -> 0;
        };
    }

    public static RuntimeException handleSQLException(SQLException exception) {
        String sqlState = exception.getSQLState();
        switch (sqlState) {
            case NOT_NULL_VIOLATION -> throw new DataNotFoundException("One or more currencies are not exist in the database");
            case UNIQUE_VIOLATION -> throw new DataAlreadyExistsException("Such currency pair exchange rate already exists");
            case NUMERIC_VALUE_OUT_OF_RANGE -> throw new IllegalArgumentException("Numeric value out of range");
            case STRING_DATA_RIGHT_TRUNCATION -> throw new IllegalArgumentException("String data is too long for the column");
            default -> throw new InternalServerException("Database Error: " + exception.getMessage());
        }
    }
}
