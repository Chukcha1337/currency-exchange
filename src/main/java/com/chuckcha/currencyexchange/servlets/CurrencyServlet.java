package com.chuckcha.currencyexchange.servlets;

import com.chuckcha.currencyexchange.exeptions.ErrorException;
import com.chuckcha.currencyexchange.dto.CurrencyDto;
import com.chuckcha.currencyexchange.services.CurrencyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@WebServlet("/currencies/*")
public class CurrencyServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ObjectMapper objectMapper = new ObjectMapper();
        String path = req.getPathInfo();

        try (PrintWriter printWriter = resp.getWriter()) {
            if (path == null || path.isEmpty()) {
                List<CurrencyDto> currencies = currencyService.findAll();
                String jsonResponse = objectMapper.writeValueAsString(currencies);
                resp.setStatus(HttpServletResponse.SC_OK);
                printWriter.write(jsonResponse);
            } else {
                String currencyCode = req.getPathInfo().substring(1);
                Optional<CurrencyDto> currency = currencyService.findCurrencyByCode(currencyCode);

                if (currency.isPresent()) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    printWriter.write(objectMapper.writeValueAsString(currency.get()));
                } else if (path.equals("/")) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    printWriter.println("There's no currency code at this URL");
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    printWriter.println("Currency not found");
                }
            }
        } catch (ErrorException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Internal Server Error");
        }
    }
}
