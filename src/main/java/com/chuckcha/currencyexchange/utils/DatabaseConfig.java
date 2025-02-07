package com.chuckcha.currencyexchange.utils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DatabaseConfig {

    private static HikariDataSource dataSource;

    private DatabaseConfig() {}

    static void loadProperties() throws InterruptedException {
        HikariConfig config = new HikariConfig("hikari.properties");
        config.setMaximumPoolSize(5);
        dataSource = new HikariDataSource(config);
        Thread.sleep(1000);
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    static void closeDataSource() {
        dataSource.close();
    }
}
