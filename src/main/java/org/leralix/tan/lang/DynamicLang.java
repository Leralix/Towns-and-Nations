package org.leralix.tan.lang;

import org.bukkit.configuration.file.YamlConfiguration;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DynamicLang {

    private static final Map<String, String> values = new HashMap<>();

    public static void loadTranslations(File folder, String fileTag) {

        if (!folder.exists()) {
            folder.mkdir();
        }

        File SpecificLangFolder = new File(folder, fileTag);
        if (!SpecificLangFolder.exists()) {
            SpecificLangFolder.mkdir();
        }

        // Since no server will create their upgrades.yml into all languages, we will use the server language here.
        LangType langType = Lang.getServerLang();

        File file = new File(SpecificLangFolder, "upgrades.yml");

        ConfigUtil.saveAndUpdateResource(TownsAndNations.getPlugin(), "lang/" + langType.getCode() + "/upgrades.yml",
                Collections.singletonList("customLang")); //blacklist the entire file

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
