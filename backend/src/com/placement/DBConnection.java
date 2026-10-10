package com.placement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // MySQL database URL
    private static final String URL =
            "jdbc:mysql://localhost:3306/placement_db";

    // MySQL username
    private static final String USER = "root";

    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    // Database connection method
    public static Connection getConnection() {

        if (PASSWORD == null || PASSWORD.isBlank()) {
            System.out.println("DB_PASSWORD environment variable is not set!");
            return null;
        }

        try {

            // MySQL JDBC Driver load
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );

            System.out.println("Database Connected Successfully!");

            return connection;

        } catch (ClassNotFoundException e) {

            System.out.println("MySQL JDBC Driver Not Found!");
            e.printStackTrace();

        } catch (SQLException e) {

            System.out.println("Database Connection Failed!");
            e.printStackTrace();

        }

        return null;
    }

    // Connection test
    public static void main(String[] args) {

        Connection connection = getConnection();

        if (connection != null) {

            System.out.println("Connection Test Successful!");

            try {
                connection.close();
                System.out.println("Database Connection Closed.");

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {

            System.out.println("Connection Test Failed!");
        }
    }
}