package com.chuckcha.currencyexchange.servlets;

import com.chuckcha.currencyexchange.dto.ExchangeDto;
import com.chuckcha.currencyexchange.services.ExchangeService;
import com.chuckcha.currencyexchange.utils.DataValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeServlet extends HttpServlet {

    private final ExchangeService exchangeService = ExchangeService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ExchangeDto> exchangeRates = exchangeService.findAll();
        makeSuccessfulResponse(resp, HttpServletResponse.SC_OK, exchangeRates);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("baseCurrency");
        String targetCurrencyCode = req.getParameter("targetCurrency");
        BigDecimal rate = BigDecimal.valueOf(Double.parseDouble(req.getParameter("rate")));
        DataValidator.validateExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);
        ExchangeDto exchangeDto = exchangeService.insertNewRate(baseCurrencyCode, targetCurrencyCode, rate);
        makeSuccessfulResponse(resp, HttpServletResponse.SC_CREATED, exchangeDto);
    }

    private void makeSuccessfulResponse(HttpServletResponse resp, int responseStatus, Object value) throws IOException {
        resp.setStatus(responseStatus);
        resp.getWriter().write(objectMapper.writeValueAsString(value));
    }
}
