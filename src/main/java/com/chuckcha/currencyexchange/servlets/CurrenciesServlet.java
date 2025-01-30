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
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (PrintWriter printWriter = resp.getWriter()) {
            List<CurrencyDto> currencies = currencyService.findAll();
            makeSuccessfulResponse(resp, objectMapper,  printWriter, HttpServletResponse.SC_OK, currencies);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Internal Server Error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String currencyName = req.getParameter("name");
        String currencyCode = req.getParameter("code");
        String currencySign = req.getParameter("sign");

        if (DataValidator.doValuesHaveNull(currencyName, currencyCode, currencySign)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("One or many values are null");
        } else if (DataValidator.isCurrencyInvalid(currencyCode, currencyName, currencySign)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Invalid currency values");
        }
        Optional<CurrencyDto> currencyDto = currencyService.insertNewCurrency(currencyCode, currencyName, currencySign);

        try (PrintWriter printWriter = resp.getWriter()) {
            if (currencyDto.isPresent()) {
                makeSuccessfulResponse(resp, objectMapper,  printWriter, HttpServletResponse.SC_CREATED, currencyDto.get());
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                printWriter.println("Invalid currency");
            }
        }
    }

    private void makeSuccessfulResponse(HttpServletResponse resp, ObjectMapper objectMapper, PrintWriter printWriter, int responseStatus, Object value ) throws IOException {
        String jsonResponse = objectMapper.writeValueAsString(value);
        resp.setStatus(responseStatus);
        printWriter.write(jsonResponse);
    }
}