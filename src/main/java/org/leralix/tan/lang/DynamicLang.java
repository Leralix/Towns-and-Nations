package org.leralix.tan.lang;

import org.bukkit.configuration.file.YamlConfiguration;
import org.leralix.tan.TownsAndNations;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DynamicLang {

    private static final Map<String, String> values = new HashMap<>();

    public static void loadTranslations(String fileTag) {
        File langFolder = new File(TownsAndNations.getPlugin().getDataFolder(), "lang");

        if (!langFolder.exists()) {
            langFolder.mkdir();
        }

        File SpecificLangFolder = new File(langFolder, fileTag);
        if(!SpecificLangFolder.exists()) {
            SpecificLangFolder.mkdir();
        }

        File file = new File(SpecificLangFolder, "upgrades.yml");

        boolean replace = ConfigUtil.getCustomConfig(ConfigTag.LANG).getBoolean("autoUpdateLangFiles",true);
        if(!file.exists() || replace) {
            TownsAndNations.getPlugin().saveResource("lang/" + fileTag + "/upgrades.yml", true);
        }
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
