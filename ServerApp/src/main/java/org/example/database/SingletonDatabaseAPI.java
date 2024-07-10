package org.example.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Settings;
import java.sql.*;


public class SingletonDatabaseAPI {

    private static SingletonDatabaseAPI single_instance = null;
    private static Connection connection = null;

    // load database connection properties
    private static final String databaseUri = Settings.DATABASE_URI;
    private static final String databaseUser = Settings.DATABASE_USERNAME;
    private static final String databasePassword = Settings.DATABASE_PASSWORD;

    // Prevent object construction to force the usage of getInstance method
    private SingletonDatabaseAPI() {
    }

    public static synchronized SingletonDatabaseAPI getInstance() {
        if (single_instance == null) {
            single_instance = new SingletonDatabaseAPI();
            initializeDatabaseConnection();
        }
        return single_instance;
    }

    private static void initializeDatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(databaseUri, databaseUser, databasePassword);
        } catch (ClassNotFoundException e) {
            // TODO: Handle ClassNotFoundException (Driver class not found)
            // e.printStackTrace(); // or log the exception
        } catch (SQLException e) {
            // TODO: Handle SQLException (Database connection error)
            // e.printStackTrace();
        }
    }

    public PreparedStatement setQuery(String query) {
        try {
            return connection.prepareStatement(query);
        } catch (SQLException sqlException) {
            // TODO: Handle SQLException (Database table/query error)
        }
        return null;
    }

    public int executeUpdate(PreparedStatement preparedStatement) {
        try {
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            // TODO: Handle SQLException (Database table/query error)
        }
        return 0;
    }

    public ResultSet executeQuery(PreparedStatement preparedStatement) {
        try {
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            // TODO: Handle SQLException (Database table/query error)
        }
        return null;
    }

}