package com.chuckcha.currencyexchange.utils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DatabaseConfig {

    private static HikariDataSource dataSource;

    private DatabaseConfig() {}

    static void loadProperties() throws InterruptedException {
        HikariConfig config = new HikariConfig("hikari.properties");
        dataSource = new HikariDataSource(config);
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    static void closeDataSource() {
        dataSource.close();
    }
}
