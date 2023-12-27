package org.tan.TownsAndNations.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.tan.TownsAndNations.TownsAndNations;

import java.io.*;
import java.util.*;

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

    public static void saveAndUpdateResource(String fileName) {

        File currentFile = new File(TownsAndNations.getPlugin().getDataFolder(),fileName);
        if (!currentFile.exists()) {
            TownsAndNations.getPlugin().saveResource(fileName, false);
        }

        InputStream baseFile = TownsAndNations.getPlugin().getResource(fileName);

        List<String> baseFileLines = new ArrayList<>();
        List<String> currentFileLines = new ArrayList<>();

        //Read base file
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(currentFile)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                currentFileLines.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Read current file
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(baseFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                baseFileLines.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateCurrentFileWithBaseFile(currentFile, baseFileLines, currentFileLines);

    }



    public static void updateCurrentFileWithBaseFile(File file, List<String> baseFileLines, List<String> currentFileLines) {
        // Créer une map pour stocker les clés et les lignes du fichier actuel
        Map<String, String> currentFileMap = new HashMap<>();
        for (String line : currentFileLines) {
            String key = line.contains(":") ? line.split(":")[0].trim() : line;
            currentFileMap.put(key, line);
        }

        // Parcourir les lignes du fichier de base et les comparer
        for (String baseLine : baseFileLines) {
            String baseKey = baseLine.contains(":") ? baseLine.split(":")[0].trim() : baseLine;
            if (!currentFileMap.containsKey(baseKey)) {
                // Si la clé n'est pas présente dans le fichier actuel, ajouter la ligne
                TownsAndNations.getPluginLogger().warning("Adding config line : " + baseLine + " in config file");
                currentFileLines.add(baseLine);
            }
        }
        writeToFile(currentFileLines, file);
    }

    public static void writeToFile(List<String> lines, File fileToWrite) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToWrite, false))) { // false pour écraser le contenu existant
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
