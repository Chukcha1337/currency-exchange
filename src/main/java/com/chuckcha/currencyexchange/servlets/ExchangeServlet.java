package com.chuckcha.currencyexchange.servlets;

import com.chuckcha.currencyexchange.dto.CurrencyDto;
import com.chuckcha.currencyexchange.dto.ExchangeDto;
import com.chuckcha.currencyexchange.services.ExchangeService;
import com.chuckcha.currencyexchange.utils.DataValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@WebServlet("/exchangeRates")
public class ExchangeServlet extends HttpServlet {

    private final ExchangeService exchangeService = ExchangeService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (PrintWriter printWriter = resp.getWriter()) {
            List<ExchangeDto> exchangeRates = exchangeService.findAll();
            makeSuccessfulResponse(resp, objectMapper, printWriter, HttpServletResponse.SC_OK, exchangeRates);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Internal Server Error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("baseCurrency");
        String targetCurrencyCode = req.getParameter("targetCurrency");
        BigDecimal rate = BigDecimal.valueOf(Double.parseDouble(req.getParameter("rate")));

        if (DataValidator.doValuesHaveNull(baseCurrencyCode, targetCurrencyCode, rate)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("One or many values are null");
        } else if (DataValidator.isRateInvalid(baseCurrencyCode, targetCurrencyCode)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Invalid currencies values");
        }

        Optional<ExchangeDto> exchangeDto = exchangeService.insertNewRate(baseCurrencyCode, targetCurrencyCode, rate);

        try (PrintWriter printWriter = resp.getWriter()) {
            if (exchangeDto.isPresent()) {
                makeSuccessfulResponse(resp, objectMapper, printWriter, HttpServletResponse.SC_CREATED, exchangeDto.get());
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                printWriter.println("Invalid data");
            }
        }
    }

    private void makeSuccessfulResponse(HttpServletResponse resp, ObjectMapper objectMapper, PrintWriter printWriter, int responseStatus, Object value) throws IOException {
        String jsonResponse = objectMapper.writeValueAsString(value);
        resp.setStatus(responseStatus);
        printWriter.write(jsonResponse);
    }
}
