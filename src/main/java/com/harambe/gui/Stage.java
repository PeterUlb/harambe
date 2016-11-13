package com.harambe.gui;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Stage class of the UI. Features the background image and theme of the stage.
 */
public class Stage {
    private String imgLocation;
    private String audioLocation;

    public Stage() {
        setRandomResources();
    }

    private void setRandomResources() {
        int rndm = ThreadLocalRandom.current().nextInt(0, 3);
        switch (rndm) {
            case 0:
                this.audioLocation = this.imgLocation = "/stages/coast_1/";
                break;
            case 1:
                this.audioLocation = this.imgLocation = "/stages/coast_2/";
                break;
            case 2:
                this.audioLocation = this.imgLocation = "/stages/star_wars_1/";
                break;
            default:
                this.audioLocation = this.imgLocation = "/stages/coast_1/";
        }

        this.imgLocation += "stage.jpg";
        this.audioLocation += "theme.mp3";
    }

    public String getImg() {
        return this.imgLocation;
    }

    public String getAudioLocation() {
        return audioLocation;
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
