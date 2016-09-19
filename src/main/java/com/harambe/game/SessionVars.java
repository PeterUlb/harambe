package com.harambe.game;

import java.util.UUID;

/**
 * Created by Peter on 16.09.2016.
 */
public class SessionVars {
    public static UUID currentGameUUID = null;
    public static String opponentPlayerName = null;
    public static int setNumber = -1; // set currently #1, #2 or #3?
    public static Boolean weStartSet = null; // did we start last set
    public static Boolean weWonSet = null; // player who won the last set
    public static int turnNumber = 0;
    public static int points = 0;

    public static void initializeNewGame(String opponentPlayerName) {
        SessionVars.currentGameUUID = UUID.randomUUID();
        SessionVars.opponentPlayerName = opponentPlayerName;
        SessionVars.setNumber = -1;
        SessionVars.turnNumber = 0;
        SessionVars.weStartSet = null;
        SessionVars.weWonSet = null;
        SessionVars.points = 0;

    }

    public static void initializeNewSet(boolean weStartSet) {
        SessionVars.setNumber++;
        SessionVars.weStartSet = weStartSet;
        SessionVars.turnNumber = 0;
    }
}
