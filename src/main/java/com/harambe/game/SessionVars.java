package com.harambe.game;

import java.util.UUID;

/**
 * TODO: insert documentation here
 */
public class SessionVars {
    //TODO ALL of them must be UI configurable
    public static boolean useFileInterface = false;
    public static boolean usePusherInterface = false;
    public static boolean soloVsAI = false;
    public static char ourSymbol = '?';  // can be 'X' or 'O', non-offline games only
    public static String fileInterfacePath = null;
    public static long timeoutThresholdInMillis = 1000; // time the algorithm has to finish
    // -------------------
    public static UUID currentGameUUID = null;
    public static String ourPlayerName = null;
    public static String opponentPlayerName = null;
    public static int setNumber = -1; // current set: #0, #1 or #2?
    public static Boolean weStartSet = null; // did we start the set
    public static Boolean weWonSet = null; // player who won the set
    public static int turnNumber = 0;

    public static void initializeNewGame(String ourPlayerName, String opponentPlayerName) {
        SessionVars.currentGameUUID = UUID.randomUUID();
        SessionVars.ourPlayerName = ourPlayerName;
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
