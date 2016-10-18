package com.harambe.game;

import java.util.ArrayList;

public abstract class ThreadManager {
    // keep track of all daemon threads
    public static ArrayList<Thread> threads = new ArrayList<>();

    /**
     * Stops all logic threads. ONLY call on screen changein MasterController
     */
    public static void reset() {
        for (Thread thread :
                threads) {
            thread.stop();
        }
    }
}
