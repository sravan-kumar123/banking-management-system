package com.banking.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Provides a single, reusable JDBC {@link Connection} to the Oracle database.
 * Connection details are externalized in {@code db.properties} on the classpath
 * (see {@code db.properties.example} for the expected format), so credentials
 * never need to be hard-coded or committed to source control.
 */
public final class DBConnection {

    private static final String CONFIG_FILE = "db.properties";
    private static volatile Connection connection;

    private DBConnection() {
        // Utility class - prevent instantiation
    }

    public static synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Properties props = loadProperties();

                String driver = props.getProperty("db.driver", "oracle.jdbc.driver.OracleDriver");
                String url = props.getProperty("db.url");
                String user = props.getProperty("db.user");
                String password = props.getProperty("db.password");

                if (url == null || user == null || password == null) {
                    throw new IllegalStateException(
                            "Missing db.url / db.user / db.password in " + CONFIG_FILE
                                    + ". Copy db.properties.example to db.properties and fill in your details.");
                }

                Class.forName(driver);
                connection = DriverManager.getConnection(url, user, password);
                connection.setAutoCommit(true); // default; service layer flips this off for multi-step operations
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Oracle JDBC driver not found on classpath.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to establish database connection.", e);
        }
        return connection;
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException(CONFIG_FILE + " not found on classpath. "
                        + "Copy src/main/resources/db.properties.example to db.properties and configure it.");
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read " + CONFIG_FILE, e);
        }
        return props;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}
