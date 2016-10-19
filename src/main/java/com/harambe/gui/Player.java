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
    private String chipImgLocation;
    private String imgLocation;

    private String dropSoundLocation;
    private String winSoundLocation;
    private String selectSoundLocation;

    private int score;

    private char symbol;

    /**
     * constructor for the player
     *
     * @param ai        boolean value for human or artificial intelligence player
     * @param name      player name
     * @param character character the player chose to play with (e.g. Harambe)
     * @param symbol    internal representation of the player chipImgLocation
     */
    public Player(boolean ai, String name, Character character, char symbol) {
        this.ai = ai;
        this.name = name;
        this.character = character.name();
        this.symbol = symbol;
        this.score = 0;

        this.setImg(character);
        this.setChipImgLocation(character);
        this.setSounds(character);
    }

    /**
     * overloads constructor to make variable name optional
     *
     * @param ai        boolean value for human or artificial intelligence player
     * @param character character the player chose to play with (e.g. Harambe)
     * @param symbol    internal representation of the player chipImgLocation
     */
    public Player(boolean ai, Character character, char symbol) {
        this(ai, character.name(), character, symbol);
    }


    //methods for playerImg
    private void setImg(Character character) {
        this.imgLocation = "characters/" + character.name().toLowerCase() + "/avatar.png";
    }

    public String getImgLocation() {
        return this.imgLocation;
    }


    /**
     * sets chipImgLocation depending on character
     *
     * @param character character the player chose to play with (e.g. Harambe)
     */
    private void setChipImgLocation(Character character) {
        this.chipImgLocation = "characters/" + character.name().toLowerCase() + "/chip.png";
    }

    public String getChipImgLocation() {
        return this.chipImgLocation;
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
     *
     * @param character character the player chose to play with (e.g. Harambe)
     */
    private void setSounds(Character character) {
        this.dropSoundLocation = "/characters/" + character.name().toLowerCase() + "/drop.mp3";
        this.winSoundLocation = "/characters/" + character.name().toLowerCase() + "/win.mp3";
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


    public String getWinSound() {
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
