package com.chuckcha.currencyexchange.servlets;

import com.chuckcha.currencyexchange.dto.ExchangeDto;
import com.chuckcha.currencyexchange.services.ExchangeService;
import com.chuckcha.currencyexchange.utils.DataValidator;
import com.chuckcha.currencyexchange.utils.ObjectMapperSingleton;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private static final int baseCurrencyCodeFirstIndex = 1;
    private static final int targetCurrencyCodeFirstIndex = 4;
    private final ExchangeService exchangeService = ExchangeService.getInstance();
    private final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            this.doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        DataValidator.validatePath(path);
        String baseCurrencyCode = req.getPathInfo().substring(baseCurrencyCodeFirstIndex, targetCurrencyCodeFirstIndex);
        String targetCurrencyCode = req.getPathInfo().substring(targetCurrencyCodeFirstIndex);
        ExchangeDto exchangeRate = exchangeService.findExchangeRateByCode(baseCurrencyCode, targetCurrencyCode);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(objectMapper.writeValueAsString(exchangeRate));
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        String body = req.getReader().readLine();
        DataValidator.validateBody(body);
        Map<String, String> parameters = Arrays.stream(body.split("&"))
                .map(param -> param.split("=", 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(parts -> parts[0], parts -> URLDecoder.decode(parts[1], StandardCharsets.UTF_8), (a, b) -> b));
        String rate = parameters.get("rate");
        DataValidator.validateExchangeRate(rate);
        DataValidator.validatePath(path);
        String baseCurrencyCode = req.getPathInfo().substring(baseCurrencyCodeFirstIndex, targetCurrencyCodeFirstIndex);
        String targetCurrencyCode = req.getPathInfo().substring(targetCurrencyCodeFirstIndex);
        ExchangeDto exchangeRate = exchangeService.updateExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(objectMapper.writeValueAsString(exchangeRate));
    }
}


