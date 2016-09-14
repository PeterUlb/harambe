package com.harambe.gui;

public class Chip {
    private String imgLocation;

    public Chip(String chip) {
        setImg(chip);
    }


    private void setImg(String chip) {
        this.imgLocation = "/img/"+chip+".png";
    }

    public String getImg() {
        return this.imgLocation;
    }


}
