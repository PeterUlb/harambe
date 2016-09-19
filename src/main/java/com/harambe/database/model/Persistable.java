package com.harambe.database.model;

import com.harambe.database.DatabaseConnector;

import java.sql.SQLException;

/**
 * Created by Peter on 19.09.2016.
 */
public interface Persistable {
    public void persistInDatabase(DatabaseConnector db) throws SQLException;
}
