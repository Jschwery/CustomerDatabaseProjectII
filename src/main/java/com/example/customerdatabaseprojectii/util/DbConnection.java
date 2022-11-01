package com.example.customerdatabaseprojectii.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class DbConnection {
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER";
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String userName = "root";
    private static String password = "Bearsfan580!";
   private static Connection connection = null;
   private static PreparedStatement preparedStatement;
    public static void makeConnection() {
        try {
            Class.forName(driver);
            password = "Bearsfan580!";
            connection = DriverManager.getConnection(jdbcUrl, userName, password);
            System.out.println("Connection successful!");
        } catch (ClassNotFoundException e) {
            System.out.println("Error:" + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
    }
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
    public static void makePreparedStatement(String sqlStatement, Connection conn) throws SQLException {
        if (conn != null) preparedStatement = conn.prepareStatement(sqlStatement);
        else System.out.println("Prepared Statement Creation Failed!");
    }
    public static PreparedStatement getPreparedStatement() throws SQLException {
        if (preparedStatement != null) return preparedStatement;
        else System.out.println("Null reference to Prepared Statement");
        return null;
    }
}