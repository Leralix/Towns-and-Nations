package org.tan.towns_and_nations.Lang;

import org.bukkit.configuration.file.YamlConfiguration;
import org.tan.towns_and_nations.TownsAndNations;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public enum Lang {
    WELCOME,
    LANGUAGE_SUCCESSFULLY_LOADED,
    PLUGIN_STRING;

    private static final Map<Lang, String> translations = new HashMap<>();

    public static void loadTranslations(String filename) {

        File langFolder = new File(TownsAndNations.getPlugin().getDataFolder(), "lang");

        if (!langFolder.exists()) {
            langFolder.mkdir();
        }

        File file = new File(langFolder, filename);

        TownsAndNations.getPlugin().saveResource("lang/" + filename, false);


        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);


        for (Lang key : Lang.values()) {

            String message = config.getString("language." + key.name());
            if (message != null) {
                translations.put(key, message);
            }
        }
    }

    public String getTranslation() {
        return translations.get(this);
    }

    public String getTranslation(Object... placeholders) {
        String translation = translations.get(this);

        if (translation != null) {
            for (int i = 0; i < placeholders.length; i++) {
                translation = translation.replace("{" + i + "}", placeholders[i].toString());
            }
        }

        return translation;
    }


}