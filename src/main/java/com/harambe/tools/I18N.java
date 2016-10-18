package com.harambe.tools;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18N {
    public static String ENGLISH = "en";
    public static String GERMAN = "de";

    private static ResourceBundle instance = ResourceBundle.getBundle("i18n.Localization", new Locale(I18N.ENGLISH));;
    public static String currentLang = instance.getLocale().getLanguage();

    private I18N() {
    }

    public static String getString(String key) {
        return instance.getString(key);
    }

    public static void setLocale(String language) {
        instance = ResourceBundle.getBundle("i18n.Localization", new Locale(language));
        currentLang = instance.getLocale().getLanguage();
    }
}
