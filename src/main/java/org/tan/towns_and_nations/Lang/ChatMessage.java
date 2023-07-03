package org.tan.towns_and_nations.Lang;

import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public enum ChatMessage {
    WELCOME,
    GOODBYE;

    private static final Map<ChatMessage, String> translations = new HashMap<>();

    public static void loadTranslations() {
        try {
            loadTranslations("english.yml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadTranslations(String filename) throws IOException {
        File file = new File(filename);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (ChatMessage key : ChatMessage.values()) {
            String message = config.getString("chatMessage." + key.name());
            if (message != null) {
                translations.put(key, message);
            }
        }
    }

    public String getTranslation() {
        return translations.get(this);
    }
}