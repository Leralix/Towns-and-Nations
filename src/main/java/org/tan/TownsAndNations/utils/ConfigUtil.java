package org.tan.TownsAndNations.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.tan.TownsAndNations.TownsAndNations;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConfigUtil {

    private static final Map<String, FileConfiguration> configs = new HashMap<>();

    public static FileConfiguration getCustomConfig(String fileName) {
        return configs.get(fileName);
    }

    public static void saveResource(String fileName) {
        File file = new File(TownsAndNations.getPlugin().getDataFolder(),fileName);
        if (!file.exists()) {
            TownsAndNations.getPlugin().saveResource(fileName, false);
        }
    }

    public static void saveAndUpdateResource(String oldFileName, String baseFileName) {
        File file = new File(TownsAndNations.getPlugin().getDataFolder(),oldFileName);
        if (!file.exists()) {
            TownsAndNations.getPlugin().saveResource(oldFileName, false);
            return;
        }

        // Lire le contenu du fichier existant
        Set<String> existingLines = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                existingLines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (InputStream is = TownsAndNations.getPlugin().getResource(baseFileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is));
             FileWriter fw = new FileWriter(file, true);
             BufferedWriter writer = new BufferedWriter(fw)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!existingLines.contains(line)) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        TownsAndNations.getPlugin().saveResource(oldFileName, true);
    }

    public static void loadCustomConfig(String fileName) {

        File configFile = new File(TownsAndNations.getPlugin().getDataFolder(), fileName);
        if (!configFile.exists()) {
            TownsAndNations.getPluginLogger().severe(fileName + " does not exist!");
            return;
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        configs.put(fileName, config);
    }



}
