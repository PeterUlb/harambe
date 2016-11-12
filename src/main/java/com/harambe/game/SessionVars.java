package com.harambe.game;

import java.util.UUID;

/**
 * This class represents a single user session, starting on program start and ending on program exit.
 * During this time, in hold all the needed user and game information which has to be shared between different controllers.
 * Information from this class is also used in the database saving process
 */
public class SessionVars {
    // access via corresponding method (makes sure that other flags are resetted)
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
    public static boolean performanceMode = false;

    /**
     * Initializes a new game, setting the flags accordingly
     * @param ourPlayerName The name of our player (e.g. Max Mustermann)
     * @param opponentPlayerName The name of our opponent (e.g. AI X)
     */
    public static void initializeNewGame(String ourPlayerName, String opponentPlayerName) {
        SessionVars.currentGameUUID = UUID.randomUUID().toString();
        SessionVars.ourPlayerName = ourPlayerName;
        SessionVars.opponentPlayerName = opponentPlayerName;
        SessionVars.setNumber = -1;
        SessionVars.turnNumber = 0;
        SessionVars.weStartSet = null;
        SessionVars.weWonSet = null;
    }

    /**
     * Initializes a new set, setting the flags accordingly
     * @param weStartSet boolean flag indicating whether we started or not
     */
    public static void initializeNewSet(boolean weStartSet) {
        SessionVars.setNumber++;
        SessionVars.weStartSet = weStartSet;
        SessionVars.turnNumber = 0;
    }

    /**
     * Resets all flags. Normally done within the SessionVars methods
     */
    public static void resetFlags() {
        SessionVars.useFileInterface = false;
        SessionVars.usePusherInterface = false;
        SessionVars.soloVsAI = false;
        SessionVars.replayMode = false;
    }

    /**
     * Starting a replay
     * @param gameUUID the replay id (equals the game id of the game which was played)
     * @param setNumber the set number being displayed
     * @param weStartSet flag indicating the player who started
     * @param ourPlayerName the name of us at the time of the game being played
     * @param opponentPlayerName the name of the enemy at the time of the game being played
     */
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

    /**
     * Setup method for an online game over the file interface
     * @param useFileInterface flag that file interface is being used
     * @param fileInterfacePath path to the server communication path
     */
    public static void useFileInterface(boolean useFileInterface, String fileInterfacePath) {
        SessionVars.resetFlags();
        SessionVars.useFileInterface = useFileInterface;
        SessionVars.fileInterfacePath = fileInterfacePath;
    }

    public static boolean getUsePusherInterface() {
        return usePusherInterface;
    }

    /**
     * Setup method for an online game over the puhser websocket interface
     * @param usePusherInterface flag that pusher interface is being used
     */
    public static void usePusherInterface(boolean usePusherInterface) {
        SessionVars.resetFlags();
        SessionVars.usePusherInterface = usePusherInterface;
    }

    public static boolean getSoloVsAI() {
        return soloVsAI;
    }

    /**
     * Setup method for human vs AI mode
     * @param soloVsAI flag
     */
    public static void soloVsAI(boolean soloVsAI) {
        SessionVars.resetFlags();
        SessionVars.soloVsAI = soloVsAI;
    }

    public static boolean getReplayMode() {
        return replayMode;
    }

    /**
     * Setup method for replay mode.
     * @param replayMode flag
     */
    private static void replayMode(boolean replayMode) {
        SessionVars.resetFlags();
        SessionVars.replayMode = replayMode;
    }

    public static String getFileInterfacePath() {
        return fileInterfacePath;
    }
}
