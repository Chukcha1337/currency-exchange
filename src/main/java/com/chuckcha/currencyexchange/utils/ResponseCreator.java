package com.chuckcha.currencyexchange.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public final class ResponseCreator {

    public static void createResponse(HttpServletResponse resp, int responseStatus, Object value, ObjectMapper mapper) throws IOException {
        resp.setStatus(responseStatus);
        resp.getWriter().write(mapper.writeValueAsString(value));
    }
}
