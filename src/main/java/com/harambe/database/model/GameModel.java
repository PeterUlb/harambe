package com.harambe.database.model;

import com.harambe.database.DatabaseConnector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by Peter on 19.09.2016.
 */
public class GameModel implements Persistable {
    private String gameUUID;
    private String ourPlayer;
    private String opponentPlayer;
    private int ourPoints;
    private int opponentPoints;
    private boolean weWon;
    private Timestamp timestamp;

    /**
     * Standard constructor for a Gamemodel object, timestamp is added on database write
     * @param gameUUID current game UUID (found in SessionVars)
     * @param ourPlayer Name of our player
     * @param opponentPlayer Name of the opponent player/team
     * @param ourPoints how many ourPoints did the we achieve in this set (saved in the Player object)
     * @param opponentPoints how many ourPoints did the opponent achieve in this set (saved in the Player object)
     * @param weWon did we win the game
     */
    public GameModel(String gameUUID, String ourPlayer, String opponentPlayer, int ourPoints, int opponentPoints, boolean weWon) {
        this.gameUUID = gameUUID;
        this.ourPlayer = ourPlayer;
        this.opponentPlayer = opponentPlayer;
        this.ourPoints = ourPoints;
        this.opponentPoints = opponentPoints;
        this.weWon = weWon;
    }

    /**
     * Constructor for database saving
     * @param gameUUID current game UUID (found in SessionVars)
     * @param ourPlayer Name of our player
     * @param opponentPlayer Name of the opponent player/team
     * @param ourPoints how many ourPoints did the we achieve in this set (saved in the Player object)
     * @param opponentPoints how many ourPoints did the opponent achieve in this set (saved in the Player object)
     * @param weWon did we win the game
     * @param timestamp basic sql timestamp
     */
    private GameModel(String gameUUID, String ourPlayer, String opponentPlayer, int ourPoints, int opponentPoints, boolean weWon, Timestamp timestamp) {
        this.gameUUID = gameUUID;
        this.ourPlayer = ourPlayer;
        this.opponentPlayer = opponentPlayer;
        this.ourPoints = ourPoints;
        this.opponentPoints = opponentPoints;
        this.weWon = weWon;
        this.timestamp = timestamp;
    }

    public static ArrayList<GameModel> getGames(DatabaseConnector db) throws SQLException {
        ArrayList<GameModel> gameModels = new ArrayList<>();
        ResultSet rs = db.query("SELECT * FROM " + DatabaseConnector.GAMETABLE);

        String gameUUID, ourPlayer, opponentPlayer;
        int ourPoints, opponentPoints;
        boolean weWon;
        Timestamp timestamp;

        while (rs.next()) {
            gameUUID = rs.getString(1);
            ourPlayer = rs.getString(2);
            opponentPlayer = rs.getString(3);
            ourPoints = rs.getInt(4);
            opponentPoints = rs.getInt(5);
            weWon = rs.getBoolean(6);
            timestamp = rs.getTimestamp(7);
            gameModels.add(new GameModel(gameUUID, ourPlayer, opponentPlayer, ourPoints, opponentPoints, weWon, timestamp));
        }

        return gameModels;
    }

    public String getGameUUID() {
        return gameUUID;
    }

    public String getOpponentPlayer() {
        return opponentPlayer;
    }

    public int getOurPoints() {
        return ourPoints;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public void persistInDatabase(DatabaseConnector db) throws SQLException {
        db.update(
                "INSERT INTO " + DatabaseConnector.GAMETABLE + " VALUES( '" + gameUUID + "', '" + ourPlayer + "', '" + opponentPlayer + "', "+ ourPoints +
                        ", " + opponentPoints + ", " + weWon + ", '" + new Timestamp(System.currentTimeMillis()) + "')");
    }
}
