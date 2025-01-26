package com.chuckcha.currencyexchange.servlets;

import com.chuckcha.currencyexchange.services.CurrencyService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@WebServlet("/currencies")
public class CurrencyServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try (PrintWriter printWriter = resp.getWriter()) {
            printWriter.write("<h1>Список валют:</h1>");
            printWriter.write("<ul>");
            currencyService.findAll().forEach(flightDto ->
                    printWriter.write("""
                            <li>
                            <a href="/currencies?id=%d">%s</a>
                            </li>
                            """.formatted(flightDto.getId(), flightDto.getDescription())));
            printWriter.write("</ul>");
        }
    }
}
