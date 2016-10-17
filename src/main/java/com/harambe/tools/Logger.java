package com.harambe.tools;

public class Logger {
    static boolean isEnabled = true;

    public static void debug(Object o) {
        if(Logger.isEnabled) {
            System.out.println("[DEBUG]: " + o.toString());
        }
    }

    public static void event(Object o) {
        if(Logger.isEnabled) {
            System.out.println("[EVENT]: " + o.toString());
        }
    }
}
