package com.example.customerdatabaseprojectii.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class DbConnection {
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER";
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String userName = "root";
    private static final String password = "Bearsfan580!";
    private static Connection connection = null;
    private static PreparedStatement preparedStatement;

    /**
     * Connects the database with the url to the database, the database username and password
     */
    public static void makeConnection() {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(jdbcUrl, userName, password);
            System.out.println("Connection successful!");
        } catch (ClassNotFoundException e) {
            System.out.println("Error:" + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
    }

    /**
     * gets the connection to the database, that is stored in the connection variable
     * @return
     */
    public static Connection getConnection() {
        return connection;
    }
    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Connection closed!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     *
     * @param sqlStatement a string to create a prepared statement with from the connection
     * @param conn the connection to the database to prepare the statement query
     * @throws SQLException
     */
    public static void makePreparedStatement(String sqlStatement, Connection conn) throws SQLException {
        if (conn != null){
            preparedStatement = conn.prepareStatement(sqlStatement);
        }
        else {
            System.out.println("Prepared Statement Creation Failed!");
        }
    }

    /**
     *
     * @return returns the prepared statement that can be executed to the database
     * @throws SQLException
     */
    public static PreparedStatement getPreparedStatement() throws SQLException {
        if (preparedStatement != null) {
            return preparedStatement;
        }
        else {
            System.out.println("Null reference to Prepared Statement");
        }
        return null;
    }

    /**
     * This is a convenience method that will get the database connection, and make the prepared statement
     * and then return the prepared statement, while only requiring the query string to be executed
     * @param query takes in a string query
     * @return returns an optional of the prepared statement to signify that there may be scenarios where
     * the prepared statement returned can be null
     * @throws SQLException
     */
    public static Optional<PreparedStatement> dbStatementTemplate(String query) throws SQLException {
        Connection contactsConnection = DbConnection.getConnection();
        DbConnection.makePreparedStatement(query, contactsConnection);
        return Optional.ofNullable(DbConnection.getPreparedStatement());
    }
}