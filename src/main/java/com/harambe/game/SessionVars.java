package com.harambe.game;

import java.util.UUID;

/**
 * Created by Peter on 16.09.2016.
 */
public class SessionVars {
    //TODO ALL of them must be UI configurable
    public static String app_id = "252455";
    public static String key = "75c96c88e0a7ef9d4c86";
    public static String secret = "667f4dd9c288a3ad97d0";
    public static boolean useFileInterface = false;
    public static boolean usePusherInterface = false;
    public static boolean soloVsAI = false;
    public static char ourSymbol = '?';  // can be 'X' or 'O', non-offline games only
    public static String fileInterfacePath = null;
    public static int searchDepth = 10;
    public static long timeoutThresholdInMillis = 1800; // after this time, a new search with outOfTimeDepth is started
    public static int outOfTimeDepth = 4; // depth of alternative alphabeta on timeout
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
