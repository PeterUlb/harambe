package com.harambe.gui;

public class Player {
    private Boolean ki;
    private String name;
    private String character;
    private String chip;
    private String imgLocation;

    public Player(Boolean ki, String name, String character) {
        this.ki = ki;
        this.name = name;
        this.character = character;

        this.setImg(character);
        this.setChip(character);
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
            case "poacher_2":   this.chip = "mango";break;
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
        return this.ki;
    }

}
