package org.tan.TownsAndNations.Lang;

import org.bukkit.configuration.file.YamlConfiguration;
import org.tan.TownsAndNations.TownsAndNations;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DynamicLang {

    private static final Map<String, String> values = new HashMap<>();

    public static void loadTranslations(String filename) {
        File langFolder = new File(TownsAndNations.getPlugin().getDataFolder(), "lang");

        if (!langFolder.exists()) {
            langFolder.mkdir();
        }

        File file = new File(langFolder, filename);

        TownsAndNations.getPlugin().saveResource("lang/" + filename, true);

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (String key : config.getKeys(false)) {
            String value = config.getString(key);
            values.put(key, value);
        }
    }

    public static String get(String key) {
        return values.get(key);
    }

}
