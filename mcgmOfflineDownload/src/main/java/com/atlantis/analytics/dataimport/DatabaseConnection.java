package com.atlantis.analytics.dataimport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.atlantis.analytics.util.PropertiesLoader;

public class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class);
    private static final String POSTGRE_URL_PREFIX = "jdbc:postgresql://";
    private PropertiesLoader propLoader = new PropertiesLoader();

    private StringBuilder dbUrlBuilder = new StringBuilder("");
    private String username = propLoader.getPostgreUsername();
    private String password = propLoader.getPostgrePassword();

    /**
     * 
     * @return connection
     */
    public Connection getConnection(String dbName) throws ClassNotFoundException {
        Connection connection = null;
        dbUrlBuilder.append(POSTGRE_URL_PREFIX).append(propLoader.getPostgreHost()).append(":")
                .append(propLoader.getPostgrePort()).append("/").append(dbName);
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(dbUrlBuilder.toString(), username, password);

            LOGGER.info("Created " + dbName + " connection ");
        } catch (ClassNotFoundException cnfe) {
            LOGGER.error("Invalid db driver class: " + cnfe);
            throw new ClassNotFoundException("Invalid db driver class");
        } catch (SQLException sqle) {
            LOGGER.error("Exception occured when creating admin connection: " + sqle);
        }

        return connection;
    }

    /**
     * 
     * @return connection
     */
    public Connection getConnection() {
        Connection connection = null;
        dbUrlBuilder.append(POSTGRE_URL_PREFIX).append(propLoader.getPostgreHost()).append(":")
                .append(propLoader.getPostgrePort()).append("/");
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(dbUrlBuilder.toString(), username, password);

            LOGGER.info("Created  connection ");
        } catch (ClassNotFoundException cnfe) {
            LOGGER.error("Invalid db driver class: " + cnfe);
        } catch (SQLException sqle) {
            LOGGER.error("Exception occured when creating admin connection: " + sqle);
        }

        return connection;
    }

    /**
     * Closing admin connection
     */
    public void closeConnection(Connection connection) {
        try {
            connection.close();
            LOGGER.info("Closing Connection");
        } catch (SQLException sqle) {
            LOGGER.error("Exception occured when closing the connection" + sqle);
        }
    }
}