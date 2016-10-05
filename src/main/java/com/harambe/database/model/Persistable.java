package com.harambe.database.model;

import com.harambe.database.DatabaseConnector;

import java.sql.SQLException;

/**
 * TODO: insert documentation here
 */
public interface Persistable {
    public void persistInDatabase(DatabaseConnector db) throws SQLException;
}
