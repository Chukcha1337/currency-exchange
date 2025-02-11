package com.chuckcha.currencyexchange.filters;

import com.chuckcha.currencyexchange.exceptions.ExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class ExceptionHandlerFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException {
        try {
            super.doFilter(req, resp, chain);
        } catch (Throwable throwable) {
            ExceptionHandler.handleExceptions(resp, throwable);
        } finally {
            resp.getWriter().close();
        }
    }
}