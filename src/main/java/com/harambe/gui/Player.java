package com.harambe.gui;

import javafx.scene.text.Text;

public class Player {
    private Boolean ai;
    private String name;
    private String character;
    private String chip;
    private String imgLocation;

    private String dropSoundLocation;
    private String winSoundLocation;
    private String selectSoundLocation;

    private int score;

    private char symbol;


    public Player(Boolean ai, String name, String character, char symbol) {
        this.ai = ai;
        this.name = name;
        this.character = character;
        this.symbol = symbol;
        this.score = 0;

        this.setImg(character);
        this.setChip(character);
        this.setSounds(character);
    }

    //overload constructor to make variable name optional
    public Player(Boolean ai, String character, char symbol) {
        this(ai, character, character, symbol);
    }



    //methods for playerImg
    private void setImg(String character) {
        this.imgLocation = "/img/"+character+".png";
    }

    public String  getImgLocation() {
        return this.imgLocation;
    }



    //methods for chipImg
    private void setChip(String character) {
        switch(character) {
            case "harambe":     this.chip = "banana";break;
            case "poacher_1":   this.chip = "mango";break;
            case "poacher_2":   this.chip = "hitmarker";break;
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


    public void setSounds(String character) {
        this.dropSoundLocation = "/audio/"+character+"_drop.mp3";
        this.winSoundLocation = "/audio/"+character+"_win.mp3";
        this.selectSoundLocation = "/audio/"+character+"_select.mp3";
    };

    public String getDropSound() {
        return dropSoundLocation;
    }

    public String getwinSound() {
        return winSoundLocation;
    }

    public String getSelectSound() {
        return selectSoundLocation;
    }

    public void incrementScore() {
        this.score++;
        System.out.println(String.valueOf(this.score));
    }

    public int getScore() {
        return this.score;
    }

}
