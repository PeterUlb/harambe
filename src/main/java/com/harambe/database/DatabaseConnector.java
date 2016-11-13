package com.harambe.database;

import java.sql.*;

public class DatabaseConnector {
    public static final String GAMETABLE = "game";
    public static final String SETTABLE = "game_set";
    public static final String TURNTABLE = "turn";
    public static final String BOARDEVALTABLE = "boardeval";
    public static final String CHARACTERTABLE = "character";


    Connection conn;

    public DatabaseConnector() throws SQLException, ClassNotFoundException {
        Class.forName("org.hsqldb.jdbcDriver");
        conn = DriverManager.getConnection("jdbc:hsqldb:"
                        + "database/db_file",    // filenames and folder location
                "root",                     // username
                "");                      // password
        initialize();
    }

    private void initialize() {
        try {
            update(
                    "CREATE TABLE IF NOT EXISTS " + GAMETABLE + " ( " +
                            "game_uuid VARCHAR(36), " +
                            "our_player VARCHAR(256)," +
                            "opponent_player VARCHAR(256)," +
                            "our_points INTEGER," +
                            "opponent_points INTEGER," +
                            "we_won BOOLEAN," +
                            "time TIMESTAMP, " +
                            "PRIMARY KEY (game_uuid)" +
                            ")"
            );

            update(
                    "CREATE TABLE IF NOT EXISTS " + BOARDEVALTABLE + " ( " +
                            "board_uuid VARCHAR(42), " +
                            "recommendedMove INTEGER," +
                            "PRIMARY KEY (board_uuid)" +
                            ")"
            );

            update(
                    "CREATE TABLE IF NOT EXISTS " + SETTABLE + " ( " +
                            "game_uuid VARCHAR(36), " +
                            "set_number INTEGER," +
                            "we_started BOOLEAN," +
                            "we_won BOOLEAN," +
                            "PRIMARY KEY (game_uuid, set_number)" +
                            ")"
            );

            update(
                    "CREATE TABLE IF NOT EXISTS " + TURNTABLE + " ( " +
                            "game_uuid VARCHAR(36), " +
                            "set_number INTEGER," +
                            "turn_number INTEGER," +
                            "is_opponent BOOLEAN," +
                            "column INTEGER," +
                            "PRIMARY KEY (game_uuid, set_number, turn_number)" +
                            ")"
            );

            update(
                    "CREATE TABLE IF NOT EXISTS " + CHARACTERTABLE + " ( " +
                            "game_uuid VARCHAR(36), " +
                            "our_player VARCHAR(256)," +
                            "opponent_player VARCHAR(256)," +
                            "PRIMARY KEY (game_uuid)" +
                            ")"
            );
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

    /**
     * Clean shutdown of database, otherwise data might be lost (when closing the program)
     * @throws SQLException General Database Exception
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
