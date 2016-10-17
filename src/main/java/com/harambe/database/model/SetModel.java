package com.harambe.database.model;

import com.harambe.database.DatabaseConnector;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * TODO: insert documentation here
 */
public class SetModel implements Persistable {
    private String gameUUID;
    private int setNumber;
    private Boolean weStarted;
    private Boolean weWon;

    /**
     *
     * @param gameUUID
     * @param setNumber starting at 0
     * @param weStarted
     * @param weWon
     */
    public SetModel(String gameUUID, int setNumber, Boolean weStarted, Boolean weWon) {
        this.gameUUID = gameUUID;
        this.setNumber = setNumber;
        this.weStarted = weStarted;
        this.weWon = weWon;
    }

    public static ArrayList<SetModel> getSets(DatabaseConnector db, String dbGameUUID) throws SQLException {
        ArrayList<SetModel> setModels = new ArrayList<>();
        ResultSet rs = db.query("SELECT * FROM " + DatabaseConnector.SETTABLE + " WHERE game_uuid = '" + dbGameUUID + "'");

        String gameUUID;
        int setNumber;
        Boolean weStarted, weWon;

        while (rs.next()) {
            gameUUID = rs.getString(1);
            setNumber = rs.getInt(2);
            weStarted = rs.getBoolean(3);
            String tmpWeWon = rs.getString(4); //rs.getBoolean returns false for null value, which is not what we want...
            if (tmpWeWon == null) {
                weWon = null;
            } else if (tmpWeWon.equals("TRUE")) {
                weWon = true;
            } else {
                weWon = false;
            }
            setModels.add(new SetModel(gameUUID, setNumber, weStarted, weWon));
        }

        return setModels;
    }

    public String getGameUUID() {
        return gameUUID;
    }

    public int getSetNumber() {
        return setNumber;
    }

    public boolean isWeStarted() {
        return weStarted;
    }

    public String getWeWon() {
        if (weWon == null) {
            return "draw";
        } else {
            return weWon.toString();
        }
    }

    @Override
    public void persistInDatabase(DatabaseConnector db) throws SQLException {
        db.update(
                "INSERT INTO " + DatabaseConnector.SETTABLE + " VALUES('" + gameUUID + "', " + setNumber + ", " + weStarted + ", " + weWon + ")");
    }
}
