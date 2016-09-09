package com.harambe.gui;

public class Chip {
    private String ImgLocation;

    public Chip(String chip) {
        setImg(chip);
    }


    private void setImg(String chip) {
        this.ImgLocation = "/img/"+chip+".png";
    }

    public String getImg() {
        return this.ImgLocation;
    }


}
