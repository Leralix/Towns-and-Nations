package org.tan.towns_and_nations.Lang;

import org.bukkit.configuration.file.YamlConfiguration;
import org.tan.towns_and_nations.TownsAndNations;

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

        File directory = TownsAndNations.getPlugin().getDataFolder(); // remplacer par le chemin du répertoire que vous voulez utiliser

        // obtenir tous les fichiers dans le répertoire
        File[] files = directory.listFiles();

        // vérifier que le répertoire n'est pas vide
        if (files != null) {
            // parcourir tous les fichiers et imprimer leurs noms
            for (File file : files) {
                System.out.println(file.getName());
            }
        } else {
            System.out.println("Le répertoire est vide ou n'existe pas.");
        }



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