package com.harambe.database.model;

import com.harambe.database.DatabaseConnector;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by Peter on 19.09.2016.
 */
public class GameModel implements Persistable {
    private String gameUUID;
    private String opponentPlayer;
    private int points;
    private Timestamp timestamp;

    public GameModel(String gameUUID, String opponentPlayer, int points) {
        this.gameUUID = gameUUID;
        this.opponentPlayer = opponentPlayer;
        this.points = points;
    }

    public GameModel(String gameUUID, String opponentPlayer, int points, Timestamp timestamp) {
        this.gameUUID = gameUUID;
        this.opponentPlayer = opponentPlayer;
        this.points = points;
        this.timestamp = timestamp;
    }

    public static ArrayList<GameModel> getGames(DatabaseConnector db) throws SQLException {
        ArrayList<GameModel> gameModels = new ArrayList<>();
        ResultSet rs = db.query("SELECT * FROM " + DatabaseConnector.GAMETABLE);

        String gameUUID, opponentPlayer;
        int points;
        Timestamp timestamp;

        while (rs.next()) {
            gameUUID = rs.getString(1);
            opponentPlayer = rs.getString(2);
            points = rs.getInt(3);
            timestamp = rs.getTimestamp(4);
            gameModels.add(new GameModel(gameUUID, opponentPlayer, points, timestamp));
        }

        return gameModels;
    }

    public String getGameUUID() {
        return gameUUID;
    }

    public String getOpponentPlayer() {
        return opponentPlayer;
    }

    public int getPoints() {
        return points;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public void persistInDatabase(DatabaseConnector db) throws SQLException {
        db.update(
                "INSERT INTO " + DatabaseConnector.GAMETABLE + " VALUES( '" + gameUUID + "', '" + opponentPlayer + "', "+ points + ", '" + new Timestamp(System.currentTimeMillis()) + "')");
    }
}
