package com.harambe.database.model;

import com.harambe.database.DatabaseConnector;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Peter on 19.09.2016.
 */
public class TurnModel implements Persistable {
    private String gameUUID;
    private int setNumber;
    private int turnNumber;
    private boolean isOpponent;
    private int column;

    /**
     *
     * @param gameUUID
     * @param setNumber starting at 0
     * @param turnNumber starting at 0
     * @param isOpponent
     * @param column
     */
    public TurnModel(String gameUUID, int setNumber, int turnNumber, boolean isOpponent, int column) {
        this.gameUUID = gameUUID;
        this.setNumber = setNumber;
        this.turnNumber = turnNumber;
        this.isOpponent = isOpponent;
        this.column = column;
    }

    public static ArrayList<TurnModel> getSets(DatabaseConnector db) throws SQLException {
        ArrayList<TurnModel> turnModels = new ArrayList<>();
        ResultSet rs = db.query("SELECT * FROM " + DatabaseConnector.TURNTABLE);

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
        db.update(
                "INSERT INTO " + DatabaseConnector.TURNTABLE + " VALUES('" + gameUUID + "', " + setNumber + ", " + turnNumber + ", '" + isOpponent + "', " + column +  ")");
    }
}
