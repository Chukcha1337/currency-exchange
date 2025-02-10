package com.chuckcha.currencyexchange.dao;

import com.chuckcha.currencyexchange.utils.DatabaseConfig;
import com.chuckcha.currencyexchange.utils.ExceptionHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractDao {

    protected <T> T executeQuery(String sql, SQLConsumer<PreparedStatement> parameterSetter, SQLFunction<ResultSet, T> resultProcessor) {
        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            parameterSetter.accept(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultProcessor.apply(resultSet);
        } catch (SQLException e) {
            throw ExceptionHandler.handleSQLException(e);
        }
    }

    protected void executeUpdate(String sql, SQLConsumer<PreparedStatement> parameterSetter) {
        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            parameterSetter.accept(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw ExceptionHandler.handleSQLException(e);
        }
    }
}
