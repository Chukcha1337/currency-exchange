package com.chuckcha.currencyexchange.servlets;

import com.chuckcha.currencyexchange.dto.CurrencyDto;
import com.chuckcha.currencyexchange.services.CurrencyService;
import com.chuckcha.currencyexchange.utils.DataValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String path = req.getPathInfo();
        DataValidator.validatePath(path);
        String currencyCode = req.getPathInfo().substring(1);
        CurrencyDto currency = currencyService.findCurrencyByCode(currencyCode);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(objectMapper.writeValueAsString(currency));
    }
}
