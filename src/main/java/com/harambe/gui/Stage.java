package com.harambe.gui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Stage class of the UI. Features the background image and theme of the stage.
 */
public class Stage {
    private String imgLocation;

    public Stage() {
        setRandomImg();
    }

    private void setRandomImg() {
        int rndm = ThreadLocalRandom.current().nextInt(0, 3);
        switch (rndm) {
            case 0:
                this.imgLocation = "/img/stages/coast_1.jpg";
                break;
            case 1:
                this.imgLocation = "/img/stages/coast_2.jpg";
                break;
            case 2:
                this.imgLocation = "/img/stages/star_wars_1.jpg";
                break;
            default:
                this.imgLocation = "/img/stages/coast_1.jpg";
        }
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
        return "/img/monkey.png";
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
