package com.chuckcha.currencyexchange.config;

import com.chuckcha.currencyexchange.dto.CurrencyDto;
import com.chuckcha.currencyexchange.dto.ExchangeDto;
import com.chuckcha.currencyexchange.services.impl.CurrencyServiceImpl;
import com.chuckcha.currencyexchange.services.ExchangeService;
import com.chuckcha.currencyexchange.services.impl.ExchangeServiceImpl;
import com.chuckcha.currencyexchange.services.CurrencyService;
import com.chuckcha.currencyexchange.utils.ScriptReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.SneakyThrows;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;


@WebListener
public final class ApplicationInitializer implements ServletContextListener {

    @SneakyThrows
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DatabaseConfig.loadProperties();
        ScriptReader.executeInitScripts();
        ServletContext context = sce.getServletContext();
        ObjectMapper objectMapper = new ObjectMapper();
        CurrencyService<CurrencyDto> currencyService = CurrencyServiceImpl.getInstance();
        ExchangeService<ExchangeDto> exchangeService = ExchangeServiceImpl.getInstance();
        context.setAttribute("currencyService", currencyService);
        context.setAttribute("objectMapper", objectMapper);
        context.setAttribute("exchangeService", exchangeService);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ScriptReader.executeDeleteScripts();
        DatabaseConfig.closeDataSource();
        try {
            Driver driver = DriverManager.getDriver("jdbc:postgresql://");
            DriverManager.deregisterDriver(driver);
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении драйвера PostgreSQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
