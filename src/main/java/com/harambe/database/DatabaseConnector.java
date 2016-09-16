package com.harambe.database;

import java.sql.*;

/**
 * Created by Peter on 16.09.2016.
 */
public class DatabaseConnector {
    Connection conn;

    public DatabaseConnector() throws Exception {
        Class.forName("org.hsqldb.jdbcDriver");
        conn = DriverManager.getConnection("jdbc:hsqldb:"
                        + "database/db_file",    // filenames and folder location
                "root",                     // username
                "");                      // password
    }

    /**
     * Clean shutdown of database, otherwise data might be lost (when closing the program)
     * @throws SQLException
     */
    public void shutdown() throws SQLException {
        Statement st = conn.createStatement();
        st.execute("SHUTDOWN");
        conn.close();
    }

    /**
     * Use for SQL command SELECT
     * @param query The SQL query
     * @return ResultSet Of the Query
     * @throws SQLException e.g. user lacks privilege or object not found
     */
    public synchronized ResultSet query(String query) throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        statement.close();
        return resultSet;
    }

    /**
     * Use for SQL commands CREATE, DROP, INSERT and UPDATE
     * @param query The SQL query
     * @throws SQLException e.g. user lacks privilege or object not found
     */
    public synchronized void update(String query) throws SQLException {
        Statement statement = conn.createStatement();
        statement.executeUpdate(query);
        statement.close();
    }
}
