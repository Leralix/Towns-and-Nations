package org.leralix.tan.lang;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;

import java.io.File;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class DynamicLang {

    private static LangType serverLang;

    private static final EnumMap<LangType ,Map<String, String>> values = new EnumMap<>(LangType.class);

    public static void loadTranslations(File folder, String fileTag) {

        serverLang = LangType.fromCode(fileTag);


        if (!folder.exists()) {
            folder.mkdir();
        }

        for(LangType langType : LangType.values()) {

            File SpecificLangFolder = new File(folder, langType.getCode());
            if (!SpecificLangFolder.exists()) {
                SpecificLangFolder.mkdir();
            }

            File file = new File(SpecificLangFolder, "upgrades.yml");

            ConfigUtil.saveAndUpdateResource(TownsAndNations.getPlugin(), "lang/" + langType.getCode() + "/upgrades.yml",
                    Collections.singletonList("customLang")); //blacklist the entire file

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            Map<String, String> values = new HashMap<>();

            ConfigurationSection configurationSection = config.getConfigurationSection("customLang");
            if(configurationSection == null) {
                DynamicLang.values.put(langType, Collections.emptyMap());
                continue;
            }

            for (String key : configurationSection.getKeys(false)) {
                String value = configurationSection.getString(key);
                values.put(key, value);
            }

            DynamicLang.values.put(langType, values);
        }



    }

    public static String get(LangType langType, String key) {
        String line = values.get(langType).get(key);
        if(line == null){
            if(langType == serverLang) {
                return key;
            }
            return get(serverLang, key);
        }
        return line;
    }

}
