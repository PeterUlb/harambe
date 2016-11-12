package com.harambe.database.model;

import com.harambe.database.DatabaseConnector;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class represents the character game relationship in the database.
 * It is used to display the correct characters in replays
 */
public class CharacterModel implements Persistable {
    private String gameUUID;
    private String ourCharacter;
    private String opponentCharacter;

    /**
     *
     * @param gameUUID The game-uuid of the game to be saved
     * @param ourCharacter one value of Character enum
     * @param opponentCharacter one value of Character enum
     */
    public CharacterModel(String gameUUID, String ourCharacter, String opponentCharacter) {
        this.gameUUID = gameUUID;
        this.ourCharacter = ourCharacter;
        this.opponentCharacter = opponentCharacter;
    }

    /**
     * Returns the Character Database Entry connection a gameid with the characters selected by player1 and player2
     * @param db The database handler
     * @param dbGameUUID The game-uuid of the game in question
     * @return CharacterModel with <b>gameuuid, character name 1, character name 2</b>
     * @throws SQLException generic Database Error
     */
    public static CharacterModel getCharacter(DatabaseConnector db, String dbGameUUID) throws SQLException {
        CharacterModel characterModel;
        ResultSet rs = db.query("SELECT * FROM " + DatabaseConnector.CHARACTERTABLE + " WHERE game_uuid = '" + dbGameUUID + "'");

        String gameUUID, ourCharacter, opponentCharacter;

        rs.next(); // ResultSet is positioned before first row
        gameUUID = rs.getString(1);
        ourCharacter = rs.getString(2);
        opponentCharacter = rs.getString(3);
        characterModel = new CharacterModel(gameUUID, ourCharacter, opponentCharacter);

        return characterModel;
    }

    public String getGameUUID() {
        return gameUUID;
    }

    public void setGameUUID(String gameUUID) {
        this.gameUUID = gameUUID;
    }

    public String getOurCharacter() {
        return ourCharacter;
    }

    public String getOpponentCharacter() {
        return opponentCharacter;
    }

    @Override
    public void persistInDatabase(DatabaseConnector db) throws SQLException {
        db.update(
                "INSERT INTO " + DatabaseConnector.CHARACTERTABLE + " VALUES('" + gameUUID + "', '" + ourCharacter + "', '" + opponentCharacter + "')");
    }
}
