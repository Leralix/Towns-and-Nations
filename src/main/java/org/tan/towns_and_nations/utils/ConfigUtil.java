package org.tan.towns_and_nations.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.tan.towns_and_nations.TownsAndNations;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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
