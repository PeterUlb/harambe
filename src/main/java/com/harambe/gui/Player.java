package com.harambe.gui;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

/**
 * Player class of the UI. Features the name, the character and all the parameters concerning those items.
 */
public class Player {
    private boolean ai;
    private String name;
    private String character;
    private String chip;
    private String imgLocation;

    private String dropSoundLocation;
    private String winSoundLocation;
    private String selectSoundLocation;

    private int score;

    private char symbol;

    /**
     * constructor for the player
     * @param ai boolean value for human or artificial intelligence player
     * @param name player name
     * @param character character the player chose to play with (e.g. Harambe)
     * @param symbol internal representation of the player chip
     */
    public Player(boolean ai, String name, String character, char symbol) {
        this.ai = ai;
        this.name = name;
        this.character = character;
        this.symbol = symbol;
        this.score = 0;

        this.setImg(character);
        this.setChip(character);
        this.setSounds(character);
    }

    /**
     * overloads constructor to make variable name optional
     * @param ai boolean value for human or artificial intelligence player
     * @param character character the player chose to play with (e.g. Harambe)
     * @param symbol internal representation of the player chip
     */
    public Player(boolean ai, String character, char symbol) {
        this(ai, character, character, symbol);
    }



    //methods for playerImg
    private void setImg(String character) {
        this.imgLocation = "img/"+character+".png";
    }

    public String  getImgLocation() {
        return this.imgLocation;
    }


    /**
     * sets chip depending on character
     * @param character character the player chose to play with (e.g. Harambe)
     */
    private void setChip(String character) {
        switch(character) {
            case "harambe":     this.chip = "banana";break;
            case "poacher_1":   this.chip = "mango";break;
            case "poacher_2":   this.chip = "bullets";break;
        }
    }

    public String getChip() {
        return this.chip;
    }


    //other getters
    public String getName() {
        return this.name;
    }

    public String getCharacter() {
        return this.character;
    }

    public boolean getKi() {
        return this.ai;
    }

    public char getSymbol() {
        return this.symbol;
    }

    /**
     * sets the 3 sounds for the chosen character
     * @param character character the player chose to play with (e.g. Harambe)
     */
    private void setSounds(String character) {
        this.dropSoundLocation = "/audio/"+character+"_drop.mp3";
        this.winSoundLocation = "/audio/"+character+"_win.mp3";
        this.selectSoundLocation = "/audio/"+character+"_select.mp3";
    }

    public void playDropSound() {
        final URL resource = getClass().getResource(dropSoundLocation);
        final Media drop = new Media(resource.toString());
        MediaPlayer player = new MediaPlayer(drop);
        player.play();
    }

    public void playWinSound() {
            final URL resource = getClass().getResource(winSoundLocation);
            final Media drop = new Media(resource.toString());
            MediaPlayer player = new MediaPlayer(drop);
            player.play();
    }

    public void playSelectSound() {
            final URL resource = getClass().getResource(selectSoundLocation);
            final Media drop = new Media(resource.toString());
            MediaPlayer player = new MediaPlayer(drop);
            player.play();
    }


    public String getwinSound() {
        return winSoundLocation;
    }

    public String getSelectSound() {
        return selectSoundLocation;
    }

    public void incrementScore() {
        this.score++;
//        System.out.println(String.valueOf(this.score));
    }

    public int getScore() {
        return this.score;
    }

}
