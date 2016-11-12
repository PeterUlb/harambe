package com.harambe.gui;


import com.harambe.tools.I18N;
import com.harambe.tools.Logger;
import sun.misc.Launcher;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Character Class for all character & chip images
 */
public class Character {
    static {
        initialize();
    }
    public static String[] characters;

    public static String getLocalizedCharacterName(int index) {
        return I18N.getString(characters[index]);
    }

    private static void initialize() {
        try {
            ArrayList<String> strings = new ArrayList<>();
            final String path = "characters";
            final File jarFile = new File(Paths.get(Character.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toFile().toString());

            if (jarFile.isFile()) {  // JAR code
                final JarFile jar = new JarFile(jarFile);
                final Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    String name = entries.nextElement().getName();
                    if (name.startsWith(path + "/") && name.endsWith("/")) {
                        name = name.replace("characters/", "").replace("/", "");
                        if (name.length() != 0) {
                            strings.add(name);
                        }
                    }
                }
                jar.close();
            } else { // IDE code
                final URL url = Launcher.class.getResource("/" + path);
                if (url != null) {
                    final File apps = new File(url.toURI());
                    for (File app : apps.listFiles()) {
                        if (app.isDirectory()) {
                            strings.add(app.getName());
                        }
                    }
                }
            }
            characters = strings.toArray(new String[0]);
            Logger.debug("Initialized " + characters.length + " characters.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
