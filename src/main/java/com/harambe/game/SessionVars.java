package com.harambe.game;

import java.util.UUID;

/**
 * Created by Peter on 16.09.2016.
 */
public class SessionVars {
    public static UUID currentGameUUID = null;
    public static String ourPlayer = null;
    public static String opponentPlayerName = null;
    public static int setNumber = -1; // current set: #0, #1 or #2?
    public static Boolean weStartSet = null; // did we start the set
    public static Boolean weWonSet = null; // player who won the set
    public static int turnNumber = 0;

    public static void initializeNewGame(String ourPlayer, String opponentPlayerName) {
        SessionVars.currentGameUUID = UUID.randomUUID();
        SessionVars.ourPlayer = ourPlayer;
        SessionVars.opponentPlayerName = opponentPlayerName;
        SessionVars.setNumber = -1;
        SessionVars.turnNumber = 0;
        SessionVars.weStartSet = null;
        SessionVars.weWonSet = null;

    }

    public static void initializeNewSet(boolean weStartSet) {
        SessionVars.setNumber++;
        SessionVars.weStartSet = weStartSet;
        SessionVars.turnNumber = 0;
    }
}
