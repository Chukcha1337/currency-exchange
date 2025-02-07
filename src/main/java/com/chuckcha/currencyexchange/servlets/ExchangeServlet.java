package com.chuckcha.currencyexchange.servlets;

import com.chuckcha.currencyexchange.dto.ExchangeDto;
import com.chuckcha.currencyexchange.dto.ExchangeOperationDto;
import com.chuckcha.currencyexchange.services.ExchangeService;
import com.chuckcha.currencyexchange.utils.DataValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    private ExchangeService<ExchangeDto> service;
    private ObjectMapper mapper;

    @Override
    public void init(ServletConfig config) {
        service = (ExchangeService<ExchangeDto>) config.getServletContext().getAttribute("exchangeService");
        mapper = (ObjectMapper) config.getServletContext().getAttribute("objectMapper");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("from");
        String targetCurrencyCode = req.getParameter("to");
        String stringAmount = req.getParameter("amount");

        DataValidator.validateExchangeRate(baseCurrencyCode, targetCurrencyCode, stringAmount);
        ExchangeOperationDto result = service.doExchangeOperation(baseCurrencyCode, targetCurrencyCode, stringAmount);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(mapper.writeValueAsString(result));
    }
}
