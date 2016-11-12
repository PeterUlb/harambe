package com.harambe.database.model;

import com.harambe.database.DatabaseConnector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class represents a turn in the database, storing the linked gameID, linked set number, the incremental
 * turn number, which player played the stored turn and in which column the chip was dropped.
 */
public class TurnModel implements Persistable {
    private String gameUUID;
    private int setNumber;
    private int turnNumber;
    private boolean isOpponent;
    private int column;

    /**
     *
     * @param gameUUID gameID of the game the turn belongs to
     * @param setNumber starting at 0
     * @param turnNumber starting at 0
     * @param isOpponent flag indicating who played the turn
     * @param column column in which the chip was dropped
     */
    public TurnModel(String gameUUID, int setNumber, int turnNumber, boolean isOpponent, int column) {
        this.gameUUID = gameUUID;
        this.setNumber = setNumber;
        this.turnNumber = turnNumber;
        this.isOpponent = isOpponent;
        this.column = column;
    }

    /**
     * Returns a list of all games from the database
     * @param db The database handler
     * @param dbGameUUID gameid of the corresponding game
     * @param dbSetNumber set number of the corresponding set
     * @return ArrayList of all Games in the database
     * @throws SQLException Generic Database Exception
     */
    public static ArrayList<TurnModel> getTurns(DatabaseConnector db, String dbGameUUID, int dbSetNumber) throws SQLException {
        ArrayList<TurnModel> turnModels = new ArrayList<>();
        ResultSet rs = db.query("SELECT * FROM " + DatabaseConnector.TURNTABLE + " WHERE game_uuid = '" + dbGameUUID + "' AND set_number = " + dbSetNumber);

        String gameUUID;
        int setNumber, turnNumber;
        boolean isOpponent;
        int column;

        while (rs.next()) {
            gameUUID = rs.getString(1);
            setNumber = rs.getInt(2);
            turnNumber = rs.getInt(3);
            isOpponent = rs.getBoolean(4);
            column = rs.getInt(5);
            turnModels.add(new TurnModel(gameUUID, setNumber, turnNumber, isOpponent, column));
        }

        return turnModels;
    }

    public String getGameUUID() {
        return gameUUID;
    }

    public int getSetNumber() {
        return setNumber;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public boolean isOpponent() {
        return isOpponent;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public void persistInDatabase(DatabaseConnector db) throws SQLException {
        db.update("INSERT INTO " + DatabaseConnector.TURNTABLE + " VALUES('" + gameUUID + "', " + setNumber + ", " + turnNumber + ", " + isOpponent + ", " + column +  ")");
    }
}
