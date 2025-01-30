package com.chuckcha.currencyexchange.filters;

import com.chuckcha.currencyexchange.exceptions.InvalidValueException;
import com.chuckcha.currencyexchange.exceptions.NullInsertException;
import com.chuckcha.currencyexchange.utils.DataValidator;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebFilter({"/currency/*", "/currencies"})
public class CurrencyValidationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException, NullInsertException, InvalidValueException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("application/json");

        if (req.getMethod().equals("POST")) {
            String currencyName = req.getParameter("name");
            String currencyCode = req.getParameter("code");
            String currencySign = req.getParameter("sign");

            if (DataValidator.doValuesHaveNull(currencyName, currencyCode, currencySign)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("One or many values are null");
            } else if (DataValidator.isCurrencyInvalid(currencyCode, currencyName, currencySign)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("Invalid currency values");
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
