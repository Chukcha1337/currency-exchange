package com.chuckcha.currencyexchange.filters;

import com.chuckcha.currencyexchange.exceptions.CurrencyAlreadyExistsException;
import com.chuckcha.currencyexchange.exceptions.NullInsertException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Priority;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@WebFilter("/*")
public class ExceptionHandlerFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException {
        servletRequest.setCharacterEncoding(StandardCharsets.UTF_8.name());
        servletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());

        servletResponse.setContentType("application/json");

        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        PrintWriter printWriter = resp.getWriter();
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (NullInsertException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            printWriter.write("One or many fields are null");
        } catch (CurrencyAlreadyExistsException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            printWriter.write("Currency already exists");
        } catch (ServletException | IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            printWriter.write("Internal Server Error");
        }  finally {
            printWriter.flush();
        }
    }
}
