package com.chuckcha.currencyexchange.servlets;

import com.chuckcha.currencyexchange.dto.CurrencyDto;
import com.chuckcha.currencyexchange.services.Service;
import com.chuckcha.currencyexchange.utils.DataValidator;
import com.chuckcha.currencyexchange.utils.ResponseCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private Service<CurrencyDto> service;
    private ObjectMapper mapper;

    @Override
    public void init(ServletConfig config) {
        service = (Service<CurrencyDto>) config.getServletContext().getAttribute("currencyService");
        mapper = (ObjectMapper) config.getServletContext().getAttribute("objectMapper");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        DataValidator.validatePath(path);
        String currencyCode = req.getPathInfo().substring(1);
        CurrencyDto currency = service.findByCode(currencyCode);
        ResponseCreator.createResponse(resp, HttpServletResponse.SC_OK, currency, mapper);
    }
}
