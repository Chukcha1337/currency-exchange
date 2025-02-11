package com.chuckcha.currencyexchange.servlets;

import com.chuckcha.currencyexchange.dto.CurrencyDto;
import com.chuckcha.currencyexchange.services.CurrencyService;
import com.chuckcha.currencyexchange.utils.DataValidator;
import com.chuckcha.currencyexchange.utils.ResponseCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private CurrencyService<CurrencyDto> service;
    private ObjectMapper mapper;

    @Override
    public void init(ServletConfig config) {
        service = (CurrencyService<CurrencyDto>) config.getServletContext().getAttribute("currencyService");
        mapper = (ObjectMapper) config.getServletContext().getAttribute("objectMapper");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<CurrencyDto> currencies = service.findAll();
        ResponseCreator.createResponse(resp, HttpServletResponse.SC_OK, currencies, mapper);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String currencyName = req.getParameter("name");
        String currencyCode = req.getParameter("code");
        String currencySign = req.getParameter("sign");
        DataValidator.validateCurrency(currencyName, currencyCode, currencySign);
        CurrencyDto currencyDto = service.insertNewValue(currencyCode, currencyName, currencySign);
        ResponseCreator.createResponse(resp, HttpServletResponse.SC_CREATED, currencyDto, mapper);
    }

}