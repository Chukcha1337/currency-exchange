package com.chuckcha.currencyexchange.filters;

import com.chuckcha.currencyexchange.utils.ExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class ExceptionHandlerFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException {
        try {
            super.doFilter(req, res, chain);
        } catch (Throwable throwable) {
            ExceptionHandler.handleExeptions(res, throwable);
        } finally {
            res.getWriter().close();
        }
    }
}