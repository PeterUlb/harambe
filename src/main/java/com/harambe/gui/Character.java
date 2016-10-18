package com.harambe.gui;


import com.harambe.tools.I18N;

/**
 * Character Class for all character & chip images
 */
public class Character {

    private static final String harambe = "Harambe";
    private static final String poacher = "Poacher";
    private static final String hunter = "Hunter";
    private static final String datBoi = "DatBoi";

    public static String[] characters = {harambe, poacher, hunter, datBoi};

    public static String getLocalizedCharacterName(int index) {
        return I18N.getString(characters[index]);
    }


}
