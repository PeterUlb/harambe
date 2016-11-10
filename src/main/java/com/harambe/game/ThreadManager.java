package com.harambe.game;

import java.util.ArrayList;

public abstract class ThreadManager {
    // keep track of all daemon threads
    public static ArrayList<Thread> threads = new ArrayList<>();

    /**
     * Stops all logic threads. ONLY call on screen changein MasterController
     */
    @SuppressWarnings("deprecation")
    public static void reset() {
        threads.forEach(Thread::stop);
        threads.clear();
    }
}
