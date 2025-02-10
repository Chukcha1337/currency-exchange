package com.chuckcha.currencyexchange.utils;

import lombok.Cleanup;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public final class ScriptReader {

    private ScriptReader() {
    }

    static void initScripts() {
        executeScripts("database_init.sql", "start_values_init.sql");
    }

    static void initDeleteScripts() {
        executeScripts("database_clear.sql");
    }

    private static void executeScripts(String... scripts) {
        try {
            StringBuilder result = new StringBuilder();
            for (String script : scripts) {
                result.append(readScriptFromFile(script));
            }
            @Cleanup Connection connection = DatabaseConfig.getDataSource().getConnection();
            @Cleanup Statement statement = connection.createStatement();
            executeQueries(result.toString(), statement);
        } catch (SQLException | IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    static String readScriptFromFile(String fileName) throws IOException, URISyntaxException {
        URL resource = ScriptReader.class.getClassLoader().getResource("scripts/" + fileName);
        if (resource == null) {
            throw new FileNotFoundException("Script file not found: " + fileName);
        }
        Path path = Paths.get(resource.toURI()).toAbsolutePath();
        @Cleanup BufferedReader reader = Files.newBufferedReader(path);
        return reader.lines()
                .filter(line -> !line.trim().isEmpty() && !line.trim().startsWith("--"))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private static void executeQueries(String script, Statement statement) throws SQLException {
        String[] queries = script.split(";");
        for (String query : queries) {
            statement.addBatch(query);
        }
        statement.executeBatch();
    }
}
