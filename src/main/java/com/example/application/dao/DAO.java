package com.example.application.dao;

import java.sql.*;
import java.util.Collections;
import java.util.List;

public abstract class DAO<T> {

    private static final String DATABASE_URL = "jdbc:hsqldb:file:src/main/resources/db/recipesdb";
    private static final String DATABASE_USER = "SA";
    private static final String DATABASE_PASSWORD = "SA";

    protected String tableName;
    protected String[] columns;


    public DAO() {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
        } catch (Exception ex) {
            System.out.println("Driver error! \n"
                    + ex.getCause());
        }
    }

    protected abstract String createInsertQuery(T entity);

    protected abstract List<T> convertFrom(ResultSet resultSet) throws SQLException;

    protected abstract String createUpdateQuery(T entity);

    public boolean add(T value) {
        final String query = createInsertQuery(value);
        return executeModifyQuery(query);
    }

    public List<T> findAll() {
        final String query = String.format("select * from %s", tableName);
        return executeSelectQuery(query);
    }

    public T findById(Long id) {
        final String query = String.format("select * from %s where id = %d", tableName, id);
        List<T> result = executeSelectQuery(query);
        if (!result.isEmpty())
            return result.get(0);
        return null;
    }

    public boolean update(T value) {
        final String query = createUpdateQuery(value);
        return executeModifyQuery(query);
    }

    public boolean deleteById(Long id) {
        final String query = String.format("delete from %s where id = %d", tableName, id);
        return executeModifyQuery(query);
    }

    protected boolean executeModifyQuery(String query) {
        try {
            Connection connection = DriverManager.getConnection(
                    DATABASE_URL,
                    DATABASE_USER,
                    DATABASE_PASSWORD);
            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);
            preparedStatement.executeUpdate();
            connection.close();
            return true;
        } catch (SQLException ex) {
            System.out.println(ex.getCause() + " " + ex.getMessage());
            return false;
        }
    }


    private List<T> executeSelectQuery(String query) {
        try {
            Connection connection = DriverManager.getConnection(
                    DATABASE_URL,
                    DATABASE_USER,
                    DATABASE_PASSWORD);
            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            connection.close();
            return convertFrom(resultSet);
        } catch (SQLException ex) {
            System.out.println("Problem: "
                    + ex.getMessage() + "\n"
                    + ex.getCause());
            return Collections.emptyList();
        }
    }
}
