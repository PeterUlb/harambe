package com.harambe.gui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;

/**
 * Todo: insert docu here
 */
public class ThemePlayer {

    private MediaPlayer themePlayer;

    public void playTheme(String resourceLocation) {
        if (themePlayer != null) {
            themePlayer.stop();
        }
        final URL resource = getClass().getResource(resourceLocation);
        themePlayer = new MediaPlayer(new Media(resource.toString()));
        themePlayer.setVolume(0.05);
        themePlayer.setOnEndOfMedia(() -> themePlayer.seek(Duration.ZERO));
        themePlayer.play();
    }

    public void setMute(boolean mute) {
        themePlayer.setMute(mute);
    }

    public boolean isMute() {
        return themePlayer.isMute();
    }
}
