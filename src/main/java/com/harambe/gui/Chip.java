package com.harambe.gui;

/**
 * Chip class of the UI. Sets the image of the chip.
 */
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
