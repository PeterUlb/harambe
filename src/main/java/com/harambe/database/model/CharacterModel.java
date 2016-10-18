package com.harambe.database.model;

import com.harambe.database.DatabaseConnector;
import com.harambe.tools.I18N;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * TODO: insert documentation here
 */
public class CharacterModel implements Persistable {
    private String gameUUID;
    private String ourCharacter;
    private String opponentCharacter;

    /**
     *
     * @param gameUUID
     * @param ourCharacter one value of Character enum
     * @param opponentCharacter one value of Character enum
     */
    public CharacterModel(String gameUUID, String ourCharacter, String opponentCharacter) {
        this.gameUUID = gameUUID;
        this.ourCharacter = ourCharacter;
        this.opponentCharacter = opponentCharacter;
    }

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

    public void setOurCharacter(String ourCharacter) {
        this.ourCharacter = ourCharacter;
    }

    public String getOpponentCharacter() {
        return opponentCharacter;
    }

    public void setOpponentCharacter(String opponentCharacter) {
        this.opponentCharacter = opponentCharacter;
    }

    @Override
    public void persistInDatabase(DatabaseConnector db) throws SQLException {
        db.update(
                "INSERT INTO " + DatabaseConnector.CHARACTERTABLE + " VALUES('" + gameUUID + "', '" + ourCharacter + "', '" + opponentCharacter + "')");
    }
}
