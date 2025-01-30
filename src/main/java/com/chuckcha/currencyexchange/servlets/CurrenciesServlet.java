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
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<CurrencyDto> currencies = currencyService.findAll();
        makeSuccessfulResponse(resp, HttpServletResponse.SC_OK, currencies);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String currencyName = req.getParameter("name");
        String currencyCode = req.getParameter("code");
        String currencySign = req.getParameter("sign");
        DataValidator.validateCurrency(currencyName, currencyCode, currencySign);
        CurrencyDto currencyDto = currencyService.insertNewCurrency(currencyCode, currencyName, currencySign);
        makeSuccessfulResponse(resp, HttpServletResponse.SC_CREATED, currencyDto);
    }

    private void makeSuccessfulResponse(HttpServletResponse resp, int responseStatus, Object value) throws IOException {
        resp.setStatus(responseStatus);
        resp.getWriter().write(objectMapper.writeValueAsString(value));
    }
}