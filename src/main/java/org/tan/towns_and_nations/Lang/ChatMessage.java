package org.tan.towns_and_nations.Lang;

import org.bukkit.configuration.file.YamlConfiguration;
import org.tan.towns_and_nations.TownsAndNations;
import org.tan.towns_and_nations.utils.ConfigUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public enum ChatMessage {
    WELCOME,
    PLUGIN_STRING;

    private static final Map<ChatMessage, String> translations = new HashMap<>();



    public static void loadTranslations() {

        File langFolder = new File(TownsAndNations.getPlugin().getDataFolder(), "lang");

        if (!langFolder.exists()) {
            langFolder.mkdir();
        }
        ConfigUtil.saveResource("lang/english.yml");
        loadTranslations("lang/english.yml");

    }

    private static void loadTranslations(String filename) {


        File file = new File(filename);
        System.out.println(file);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        System.out.println(config);

        for (ChatMessage key : ChatMessage.values()) {
            String message = config.getString("chatMessage." + key.name());
            if (message != null) {
                translations.put(key, message);
            }
        }
        System.out.println(translations);
    }

    public String getTranslation() {
        return translations.get(this);
    }
}