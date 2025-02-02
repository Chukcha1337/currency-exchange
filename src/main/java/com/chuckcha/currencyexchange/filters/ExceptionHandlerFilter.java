package com.chuckcha.currencyexchange.filters;

import com.chuckcha.currencyexchange.exceptions.*;
import com.chuckcha.currencyexchange.utils.Error;
import com.chuckcha.currencyexchange.utils.ObjectMapperSingleton;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebFilter("/*")
public class ExceptionHandlerFilter implements Filter {

    private final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException {

        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("application/json");

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (NullInsertException | InvalidValueException | NumberFormatException e) {
            printError(resp,objectMapper,e.getMessage(),HttpServletResponse.SC_BAD_REQUEST);
        } catch (DataAlreadyExistsException e) {
            printError(resp,objectMapper,e.getMessage(),HttpServletResponse.SC_CONFLICT);
        } catch (DataNotExistsException | DataNotFoundException e) {
            printError(resp,objectMapper,e.getMessage(),HttpServletResponse.SC_NOT_FOUND);
        } catch (ServletException | IOException e) {
            printError(resp,objectMapper,e.getMessage(),HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }  finally {
            resp.getWriter().close();
        }
    }

    private void printError(HttpServletResponse resp, ObjectMapper objectMapper, String message, int responseStatus) throws IOException {
        resp.setStatus(responseStatus);
        resp.getWriter().write(objectMapper.writeValueAsString(new Error(message)));
    }
}
