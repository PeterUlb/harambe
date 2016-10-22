package com.harambe.gui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;

/**
 * Stage class of the UI. Features the background image and theme of the stage.
 */
public class Stage {
    private String imgLocation;
    private URL themeLocation;

    public Stage(String stageName) {
        setImg(stageName);
    }

    private void setImg(String stageName) {
        this.imgLocation = "/img/"+stageName+".jpg";
    }

    public String getImg() {
        return this.imgLocation;
    }

    /**
     * sets randomized asset image
     */
    public String getRandomAssetImg() {
        int rndm = (int) Math.round(Math.random() * 2);
        switch (rndm) {
            case 0: return "/img/tukan.png";
            case 1: return "/img/monkey.png";
            case 2: return "/img/monkey_2.png";
        }
        return "";
    }

    public String getBgAnimImg() {
        double rndm = Math.random();
        if (rndm > 0.9) {
            return "/img/swsd.png";
        }
        else {
            return "/img/seagull.png";
        }
    }


}
