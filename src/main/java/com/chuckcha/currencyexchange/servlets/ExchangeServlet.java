package com.chuckcha.currencyexchange.servlets;

import com.chuckcha.currencyexchange.dto.ExchangeOperationDto;
import com.chuckcha.currencyexchange.services.ExchangeService;
import com.chuckcha.currencyexchange.utils.DataValidator;
import com.chuckcha.currencyexchange.utils.ObjectMapperSingleton;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    private static final ExchangeService exchangeService = ExchangeService.getInstance();
    private final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("from");
        String targetCurrencyCode = req.getParameter("to");
        String stringAmount = req.getParameter("amount");

        DataValidator.validateExchangeRate(baseCurrencyCode, targetCurrencyCode, stringAmount);
        ExchangeOperationDto result = exchangeService.doExchangeOperation(baseCurrencyCode, targetCurrencyCode, stringAmount);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
