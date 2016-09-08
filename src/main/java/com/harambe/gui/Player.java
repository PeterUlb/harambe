package com.harambe.gui;

public class Player {
    private Boolean ki;
    private String name;
    private String character;
    private String imgLocation;

    public Player(Boolean ki, String name, String character) {
        this.ki = ki;
        this.name = name;
        this.character = character;

        this.setImg(this.character);
    }

    private void setImg(String character) {
        this.imgLocation = "/img/"+character+".png";
    }

    public String  getImgLocation() {
        return this.imgLocation;
    }

    public String getName() {
        return this.name;
    }

    public boolean getKi() {
        return this.ki;
    }

}
