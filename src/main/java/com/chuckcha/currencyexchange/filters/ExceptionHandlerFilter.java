package com.chuckcha.currencyexchange.filters;

import com.chuckcha.currencyexchange.exceptions.*;
import com.chuckcha.currencyexchange.dto.ErrorResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class ExceptionHandlerFilter extends HttpFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException {
        try {
            super.doFilter(req, res, chain);
        } catch (NullInsertException | InvalidValueException | NumberFormatException e) {
            printError(res,objectMapper,e.getMessage(),HttpServletResponse.SC_BAD_REQUEST);
        } catch (DataAlreadyExistsException e) {
            printError(res,objectMapper,e.getMessage(),HttpServletResponse.SC_CONFLICT);
        } catch (DataNotExistsException | DataNotFoundException e) {
            printError(res,objectMapper,e.getMessage(),HttpServletResponse.SC_NOT_FOUND);
        } catch (ServletException | IOException | RuntimeException e) {
            printError(res,objectMapper,e.getMessage(),HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }  finally {
            res.getWriter().close();
        }
    }

    private void printError(HttpServletResponse resp, ObjectMapper objectMapper, String message, int responseStatus) throws IOException {
        resp.setStatus(responseStatus);
        resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponseDto(message)));
    }
}