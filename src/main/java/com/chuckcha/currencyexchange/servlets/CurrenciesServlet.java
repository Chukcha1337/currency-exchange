package com.chuckcha.currencyexchange.servlets;

import com.chuckcha.currencyexchange.dto.CurrencyDto;
import com.chuckcha.currencyexchange.services.Service;
import com.chuckcha.currencyexchange.utils.DataValidator;
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

    private Service<CurrencyDto> service;
    private ObjectMapper mapper;

    @Override
    public void init(ServletConfig config) {
        service = (Service<CurrencyDto>) config.getServletContext().getAttribute("currencyService");
        mapper = (ObjectMapper) config.getServletContext().getAttribute("objectMapper");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<CurrencyDto> currencies = service.findAll();
        createResponse(resp, HttpServletResponse.SC_OK, currencies);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String currencyName = req.getParameter("name");
        String currencyCode = req.getParameter("code");
        String currencySign = req.getParameter("sign");
        DataValidator.validateCurrency(currencyName, currencyCode, currencySign);
        CurrencyDto currencyDto = service.insertNewValue(currencyCode, currencyName, currencySign);
        createResponse(resp, HttpServletResponse.SC_CREATED, currencyDto);
    }

    private void createResponse(HttpServletResponse resp, int responseStatus, Object value) throws IOException {
        resp.setStatus(responseStatus);
        resp.getWriter().write(mapper.writeValueAsString(value));
    }
}