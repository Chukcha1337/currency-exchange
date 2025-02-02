package com.chuckcha.currencyexchange.servlets;

import com.chuckcha.currencyexchange.dto.ExchangeDto;
import com.chuckcha.currencyexchange.services.ExchangeService;
import com.chuckcha.currencyexchange.utils.DataValidator;
import com.chuckcha.currencyexchange.utils.ObjectMapperSingleton;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeService exchangeService = ExchangeService.getInstance();
    private final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ExchangeDto> exchangeRates = exchangeService.findAll();
        makeSuccessfulResponse(resp, HttpServletResponse.SC_OK, exchangeRates);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");
        DataValidator.validateExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);
        ExchangeDto exchangeDto = exchangeService.insertNewRate(baseCurrencyCode, targetCurrencyCode, rate);
        makeSuccessfulResponse(resp, HttpServletResponse.SC_CREATED, exchangeDto);
    }

    private void makeSuccessfulResponse(HttpServletResponse resp, int responseStatus, Object value) throws IOException {
        resp.setStatus(responseStatus);
        resp.getWriter().write(objectMapper.writeValueAsString(value));
    }
}
