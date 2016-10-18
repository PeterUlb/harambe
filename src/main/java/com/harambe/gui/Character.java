package com.harambe.gui;


import com.harambe.tools.I18N;

/**
 * Character Class for all character & chip images
 */
public enum Character {
        Harambe,
        Poacher,
        Hunter,
        DatBoi,
        TheDonald;

    public static Character[] characters = Character.values();

    public static String getLocalizedCharacterName(int index) {
        return I18N.getString(characters[index].toString().toLowerCase());
    }


}
