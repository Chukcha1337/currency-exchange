package com.chuckcha.currencyexchange.filters;

import com.chuckcha.currencyexchange.exceptions.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebFilter("/*")
public class ExceptionHandlerFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException {

        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("application/json");

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (NullInsertException | InvalidValueException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(e.getMessage());
        } catch (DataAlreadyExistsException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write(e.getMessage());
        } catch (DataNotExistsException | DataNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(e.getMessage());
        } catch (ServletException | IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(e.getMessage());
        }  finally {
            resp.getWriter().close();
        }
    }
}
