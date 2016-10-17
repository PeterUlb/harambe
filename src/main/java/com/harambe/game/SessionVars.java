package com.harambe.game;

import java.util.UUID;

/**
 * TODO: insert documentation here
 */
public class SessionVars {
    //TODO ALL of them must be UI configurable
    // access via corrosponding method (makes sure that other flags are resetted)
    public static String app_id = "252455";
    public static String key = "75c96c88e0a7ef9d4c86";
    public static String secret = "667f4dd9c288a3ad97d0";
    private static boolean useFileInterface = false;
    private static String fileInterfacePath = null;
    private static boolean usePusherInterface = false;
    private static boolean soloVsAI = false;
    private static boolean replayMode = false;

    public static char ourSymbol = '?';  // can be 'X' or 'O', non-offline games only
    public static long timeoutThresholdInMillis = 1000; // time the algorithm has to finish
    // -------------------
    public static String currentGameUUID = null;
    public static String ourPlayerName = null;
    public static String opponentPlayerName = null;
    public static int setNumber = -1; // current set: #0, #1 or #2?
    public static Boolean weStartSet = null; // did we start the set
    public static Boolean weWonSet = null; // player who won the set
    public static int turnNumber = 0;

    public static void initializeNewGame(String ourPlayerName, String opponentPlayerName) {
        SessionVars.currentGameUUID = UUID.randomUUID().toString();
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

    public static void resetFlags() {
        SessionVars.useFileInterface = false;
        SessionVars.usePusherInterface = false;
        SessionVars.soloVsAI = false;
        SessionVars.replayMode = false;
    }

    public static void setupReplay(String gameUUID, int setNumber, boolean weStartSet, String ourPlayerName, String opponentPlayerName) {
        // reset all flags
        SessionVars.resetFlags();

        SessionVars.replayMode(true);
        SessionVars.currentGameUUID = gameUUID;
        SessionVars.setNumber = setNumber;
        SessionVars.weStartSet = weStartSet;
        SessionVars.ourPlayerName = ourPlayerName;
        SessionVars.opponentPlayerName = opponentPlayerName;
    }

    public static boolean getUseFileInterface() {
        return useFileInterface;
    }

    public static void useFileInterface(boolean useFileInterface, String fileInterfacePath) {
        SessionVars.resetFlags();
        SessionVars.useFileInterface = useFileInterface;
        SessionVars.fileInterfacePath = fileInterfacePath;
    }

    public static boolean getUsePusherInterface() {
        return usePusherInterface;
    }

    public static void usePusherInterface(boolean usePusherInterface) {
        SessionVars.resetFlags();
        SessionVars.usePusherInterface = usePusherInterface;
    }

    public static boolean getSoloVsAI() {
        return soloVsAI;
    }

    public static void soloVsAI(boolean soloVsAI) {
        SessionVars.resetFlags();
        SessionVars.soloVsAI = soloVsAI;
    }

    public static boolean getReplayMode() {
        return replayMode;
    }

    private static void replayMode(boolean replayMode) {
        SessionVars.resetFlags();
        SessionVars.replayMode = replayMode;
    }

    public static String getFileInterfacePath() {
        return fileInterfacePath;
    }
}
