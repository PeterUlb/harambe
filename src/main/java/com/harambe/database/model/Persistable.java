package com.harambe.database.model;

import com.harambe.database.DatabaseConnector;

import java.sql.SQLException;

/**
 * This interface serves as a contract, making sure that every Model is implementing a method which allows the storage
 * of it in the database
 */
public interface Persistable {
    /**
     * Provides logic that the implementing object is correctly stored in the database
     * @param db The database handler
     * @throws SQLException Generic Database Exception
     */
    public void persistInDatabase(DatabaseConnector db) throws SQLException;
}
