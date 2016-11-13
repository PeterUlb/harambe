package com.harambe.gui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;

/**
 * Shared Player for ambient music. Makes sure that there is one Master Player so resource usage is reduced.
 */
public class ThemePlayer {

    private MediaPlayer themePlayer;

    /**
     * Plays the specified Theme. If another Theme is already playing it is stopped.
     * There can never be two themes overlapping.
     * @param resourceLocation Path to the theme music file
     */
    public void playTheme(String resourceLocation) {
        if (themePlayer != null) {
            themePlayer.stop();
        }
        final URL resource = getClass().getResource(resourceLocation);
        themePlayer = new MediaPlayer(new Media(resource.toString()));
        themePlayer.setVolume(0.15);
        themePlayer.setOnEndOfMedia(() -> themePlayer.seek(Duration.ZERO));
        themePlayer.play();
    }

    /**
     * Mutes or unmutes the theme
     * @param mute flag indication on or off
     */
    public void setMute(boolean mute) {
        themePlayer.setMute(mute);
    }

    public boolean isMute() {
        return themePlayer.isMute();
    }
}
