package com.chuckcha.currencyexchange.servlets;

import com.chuckcha.currencyexchange.dto.CurrencyDto;
import com.chuckcha.currencyexchange.exceptions.CurrencyAlreadyExistsException;
import com.chuckcha.currencyexchange.exceptions.NullInsertException;
import com.chuckcha.currencyexchange.services.CurrencyService;
import com.chuckcha.currencyexchange.utils.DataValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try (PrintWriter printWriter = resp.getWriter()) {
            ObjectMapper objectMapper = new ObjectMapper();
            List<CurrencyDto> currencies = currencyService.findAll();
            String jsonResponse = objectMapper.writeValueAsString(currencies);
            resp.setStatus(HttpServletResponse.SC_OK);
            printWriter.write(jsonResponse);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Internal Server Error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws CurrencyAlreadyExistsException, IOException {
        String currencyName = req.getParameter("name");
        String currencyCode = req.getParameter("code");
        String currencySign = req.getParameter("sign");

        ObjectMapper objectMapper = new ObjectMapper();
        PrintWriter printWriter = resp.getWriter();
        try {
            Optional<CurrencyDto> currencyDto = currencyService.insertNewCurrency(currencyCode, currencyName, currencySign);
            if (currencyDto.isPresent()) {
                String jsonResponse = objectMapper.writeValueAsString(currencyDto.get());
                resp.setStatus(HttpServletResponse.SC_CREATED);
                printWriter.write(jsonResponse);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                printWriter.println("Invalid currency");
            }
        } finally {
            printWriter.flush();
        }
    }
}