package org.leralix.tan.utils.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used for config related utilities.
 */
public class ConfigUtil {
    /**
     * This map is used to store the custom configs.
     */
    private static final Map<ConfigTag, FileConfiguration> configs = new EnumMap<>(ConfigTag.class);

    /**
     * Get a custom config by its name.
     * @param tag       The tag of the config file.
     * @return          The {@link FileConfiguration } object.
     */
    public static FileConfiguration getCustomConfig(final @NotNull ConfigTag tag) {
        return configs.get(tag);
    }

    /**
     * Load a custom config file into the memory
     * @param fileName  The name of the file to load
     */
    public static void addCustomConfig(String fileName, ConfigTag tag) {

        File configFile = new File(TownsAndNations.getPlugin().getDataFolder(), fileName);
        if (!configFile.exists()) {
            TownsAndNations.getPlugin().getPluginLogger().severe(fileName + " does not exist!");
            return;
        }
        configs.put(tag, YamlConfiguration.loadConfiguration(configFile));
    }

    /**
     * Save and update a resource file. If some lines are missing in the current file, they will be added at the correct position.
     * @param fileName  The name of the resource file.
     */
    public static void saveAndUpdateResource(final @NotNull String fileName) {
        File currentFile = new File(TownsAndNations.getPlugin().getDataFolder(), fileName);
        if (!currentFile.exists()) {
            TownsAndNations.getPlugin().saveResource(fileName, false);
        }

        InputStream baseFile = TownsAndNations.getPlugin().getResource(fileName);

        List<String> baseFileLines = loadFileAsList(baseFile);
        List<String> currentFileLines = loadFileAsList(currentFile);

        if (baseFileLines != null && currentFileLines != null) {
            boolean updated = mergeAndPreserveLines(currentFile, baseFileLines, currentFileLines);

            if (updated) {
                TownsAndNations.getPlugin().getPluginLogger().info("The file " + fileName + " has been updated with missing lines.");
            } else {
                TownsAndNations.getPlugin().getPluginLogger().info("No updates were necessary for the file " + fileName + ".");
            }
        }
    }

    /**
     * Load a file as a list of lines.
     * @param file  The input file.
     * @return      A list of lines, or null if an error occurs.
     */
    private static List<String> loadFileAsList(InputStream file) {
        if (file == null) return null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<String> loadFileAsList(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Merge the base file lines into the current file lines, preserving order and comments.
     * @param file              The file to update.
     * @param baseFileLines     The lines from the base file.
     * @param actualFileLine  The lines from the current file.
     * @return                  True if updates were made, false otherwise.
     */
    private static boolean mergeAndPreserveLines(File file, List<String> baseFileLines, List<String> actualFileLine) {
        List<String> mergedLines = new ArrayList<>();
        Logger logger = TownsAndNations.getPlugin().getPluginLogger();

        boolean updated = false;
        int indexActual = 0;

        for(String wantedLine : baseFileLines){
            String actualLine = actualFileLine.get(indexActual);
            if(extractKey(wantedLine).equals(extractKey(actualLine))){
                mergedLines.add(actualLine);
                indexActual++;
            }
            else {
                boolean found = false;
                for(int indexActual2 = indexActual; indexActual2 < actualFileLine.size(); indexActual2++){
                    actualLine = actualFileLine.get(indexActual2);
                    if(extractKey(wantedLine).equals(extractKey(actualLine))){
                        mergedLines.add(actualLine);
                        found = true;
                        break;
                    }
                }
                if(!found){
                    logger.log(Level.INFO, "Added new config line : {0}", wantedLine);
                    mergedLines.add(wantedLine); //Line is new, save it
                    updated = true;
                }

            }
        }

        if (updated) {
            writeToFile(mergedLines, file);
        }

        return updated;
    }

    /**
     * Extracts a key from a configuration line.
     * @param line  The line to process.
     * @return      The key if found, or null otherwise.
     */
    private static String extractKey(String line) {
        if(line == null)
            return "";
        line = line.trim();
        if (line.isEmpty()) {
            return "";
        }
        if(line.startsWith("#")){
            return line;
        }
        if (line.contains(":")) {
            return line.split(":")[0].trim();
        }
        return "";
    }

    /**
     * Write a list of lines to a file.
     * @param lines         The list of lines to write.
     * @param fileToWrite   The file to write to.
     */
    private static void writeToFile(List<String> lines, File fileToWrite) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToWrite, false))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
