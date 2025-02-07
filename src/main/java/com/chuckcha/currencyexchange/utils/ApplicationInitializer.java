package com.chuckcha.currencyexchange.utils;

import com.chuckcha.currencyexchange.dto.CurrencyDto;
import com.chuckcha.currencyexchange.dto.ExchangeDto;
import com.chuckcha.currencyexchange.services.CurrencyServiceImpl;
import com.chuckcha.currencyexchange.services.ExchangeService;
import com.chuckcha.currencyexchange.services.ExchangeServiceImpl;
import com.chuckcha.currencyexchange.services.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.DriverManager;
import java.sql.SQLException;

@WebListener
public class ApplicationInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DatabaseConfig.loadProperties();
        ScriptReader.initScripts();
        ServletContext context = sce.getServletContext();
        ObjectMapper objectMapper = new ObjectMapper();
        Service<CurrencyDto> currencyService = CurrencyServiceImpl.getInstance();
        ExchangeService<ExchangeDto> exchangeService = ExchangeServiceImpl.getInstance();
        context.setAttribute("currencyService", currencyService);
        context.setAttribute("objectMapper", objectMapper);
        context.setAttribute("exchangeService", exchangeService);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ScriptReader.initDeleteScripts();
        DatabaseConfig.closeDataSource();
        try {
            DriverManager.deregisterDriver(DriverManager.getDriver("jdbc:postgresql://localhost:5432/postgres"));
        } catch (SQLException e) {
            e.printStackTrace(); // Логируйте или обрабатывайте ошибку
        }
    }
}
