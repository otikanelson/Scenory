package com.example.scenory.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
    private static DatabaseManager instance;
    private HikariDataSource dataSource;

    private DatabaseManager() {
        initializeDataSource();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initializeDataSource() {
        try {
            Properties props = loadDatabaseProperties();

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC",
                    props.getProperty("db.host"),
                    props.getProperty("db.port"),
                    props.getProperty("db.name")));
            config.setUsername(props.getProperty("db.username"));
            config.setPassword(props.getProperty("db.password"));
            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.maximum", "10")));
            config.setMinimumIdle(Integer.parseInt(props.getProperty("db.pool.minimum", "2")));
            config.setConnectionTimeout(Long.parseLong(props.getProperty("db.pool.timeout", "30000")));

            this.dataSource = new HikariDataSource(config);

            System.out.println("✅ Database connection pool initialized successfully");

        } catch (Exception e) {
            System.err.println("❌ Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Properties loadDatabaseProperties() throws IOException {
        Properties props = new Properties();

        // Try to load local config first (with actual passwords)
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database-local.properties")) {
            if (input != null) {
                props.load(input);
                System.out.println("📁 Loaded local database configuration");
                return props;
            }
        } catch (IOException e) {
            System.out.println("ℹ️ No local config found, trying default config...");
        }

        // Fallback to main config (with environment variables)
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new IOException("database.properties file not found");
            }
            props.load(input);

            // Resolve environment variables
            props = resolveEnvironmentVariables(props);
            System.out.println("📁 Loaded database configuration with environment variables");

        }
        return props;
    }

    private Properties resolveEnvironmentVariables(Properties props) {
        Properties resolved = new Properties();

        for (String key : props.stringPropertyNames()) {
            String value = props.getProperty(key);

            // Check for environment variable syntax: ${VAR_NAME:default_value}
            if (value.startsWith("${") && value.endsWith("}")) {
                String envVar = value.substring(2, value.length() - 1);
                String defaultValue = "";

                if (envVar.contains(":")) {
                    String[] parts = envVar.split(":", 2);
                    envVar = parts[0];
                    defaultValue = parts[1];
                }

                String envValue = System.getenv(envVar);
                resolved.setProperty(key, envValue != null ? envValue : defaultValue);
            } else {
                resolved.setProperty(key, value);
            }
        }

        return resolved;
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Database not initialized");
        }
        return dataSource.getConnection();
    }

    public void shutdown() {
        if (dataSource != null) {
            dataSource.close();
            System.out.println("🔒 Database connection pool closed");
        }
    }

    // Test connection method
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}