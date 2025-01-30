package com.chuckcha.currencyexchange.servlets;


import com.chuckcha.currencyexchange.dto.CurrencyDto;
import com.chuckcha.currencyexchange.dto.ExchangeDto;
import com.chuckcha.currencyexchange.services.CurrencyService;
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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private static final int baseCurrencyCodeFirstIndex = 1;
    private static final int targetCurrencyCodeFirstIndex = 4;
    private final ExchangeService exchangeService = ExchangeService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if("PATCH".equalsIgnoreCase(req.getMethod())) {
            this.doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String path = req.getPathInfo();

        try (PrintWriter printWriter = resp.getWriter()) {
            if (path == null || path.equals("/")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                printWriter.println("There are no currency codes at this URL");
            } else {
                String baseCurrencyCode = req.getPathInfo().substring(baseCurrencyCodeFirstIndex,targetCurrencyCodeFirstIndex);
                String targetCurrencyCode = req.getPathInfo().substring(targetCurrencyCodeFirstIndex);
                Optional<ExchangeDto> exchangeRate = exchangeService.findExchangeRateByCode(baseCurrencyCode, targetCurrencyCode);
                if (exchangeRate.isPresent()) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    printWriter.write(objectMapper.writeValueAsString(exchangeRate.get()));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    printWriter.println("Exchange rate for this currencies was not found");
                }
            }
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Internal Server Error");
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String path = req.getPathInfo();
        String body = req.getReader().readLine();
        Map<String, String> parameters = Arrays.stream(body.split("&"))
                .map(param -> param.split("=", 2)) // Разбиваем только на 2 части
                .filter(parts -> parts.length == 2) // Убираем некорректные
                .collect(Collectors.toMap(parts -> parts[0], parts -> URLDecoder.decode(parts[1], StandardCharsets.UTF_8), (a, b) -> b));

        String rate = parameters.get("rate");
        if (DataValidator.doValuesHaveNull(rate)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Rate is null");
        }
        BigDecimal rateDec = BigDecimal.valueOf(Double.parseDouble(rate));

        try (PrintWriter printWriter = resp.getWriter()) {
            if (path == null || path.equals("/")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                printWriter.println("There are no currency codes at this URL");
            } else {
                String baseCurrencyCode = req.getPathInfo().substring(baseCurrencyCodeFirstIndex,targetCurrencyCodeFirstIndex);
                String targetCurrencyCode = req.getPathInfo().substring(targetCurrencyCodeFirstIndex);
                Optional<ExchangeDto> exchangeRate = exchangeService.updateExchangeRate(baseCurrencyCode, targetCurrencyCode, rateDec);
                if (exchangeRate.isPresent()) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    printWriter.write(objectMapper.writeValueAsString(exchangeRate.get()));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    printWriter.println("Exchange rate for this currencies was not found");
                }
            }
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Internal Server Error");
        }
    }
}
