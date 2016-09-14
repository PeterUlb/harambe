package com.harambe.gui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;

public class Stage {
    private String imgLocation;
    private URL themeLocation;

    public Stage(String stageName) {
        setImg(stageName);
        playTheme(stageName);
    }

    private void setImg(String stageName) {
        this.imgLocation = "/img/"+stageName+".jpg";
    }

    private void playTheme(String stageName){
        // music player
        switch (stageName) {
            case "coast_1": themeLocation = getClass().getResource("/audio/mainTheme.mp3");
            case "coast_2": themeLocation = getClass().getResource("/audio/mainTheme.mp3");
        }

        final Media media = new Media(themeLocation.toString());
        final MediaPlayer player = new MediaPlayer(media);
        player.setOnEndOfMedia(() -> player.seek(Duration.ZERO));
        player.play();
    }

    public String getImg() {
        return this.imgLocation;
    }


}
