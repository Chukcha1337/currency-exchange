package com.chuckcha.currencyexchange.filters;

import com.chuckcha.currencyexchange.exceptions.InvalidValueException;
import com.chuckcha.currencyexchange.exceptions.NullInsertException;
import com.chuckcha.currencyexchange.utils.DataValidator;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@WebFilter({"/exchangeRate/*", "/exchangeRates"})
public class ExchangeValidationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException, NullInsertException, InvalidValueException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("application/json");

        if (req.getMethod().equals("POST")) {
            String baseCurrencyCode = req.getParameter("baseCurrency");
            String targetCurrencyCode = req.getParameter("targetCurrency");
            String rate = req.getParameter("rate");

            if (DataValidator.doValuesHaveNull(baseCurrencyCode, targetCurrencyCode, rate)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("One or many values are null");
            } else if (DataValidator.isRateInvalid(baseCurrencyCode, targetCurrencyCode)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("Invalid currencies values");
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        } else if (req.getMethod().equals("PATCH")) {
            String body = req.getReader().readLine();
            Map<String, String> parameters = Arrays.stream(body.split("&"))
                    .map(param -> param.split("=", 2)) // Разбиваем только на 2 части
                    .filter(parts -> parts.length == 2) // Убираем некорректные
                    .collect(Collectors.toMap(parts -> parts[0], parts -> URLDecoder.decode(parts[1], StandardCharsets.UTF_8), (a, b) -> b));

            String rate = parameters.get("rate");
            if (DataValidator.doValuesHaveNull(rate)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("Rate is null");
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
