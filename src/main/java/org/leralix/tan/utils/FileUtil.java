package org.leralix.tan.utils;

import org.leralix.tan.TownsAndNations;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

/**
 * This static class manage the archive txt for admins.
 */
public class FileUtil {
    private FileUtil() {
        throw new IllegalStateException("Utility class");
    }
    public static void addLineToHistory(final String lineToAdd) {

        if(!ConfigUtil.getCustomConfig(ConfigTag.TAN).getBoolean("archiveHistory",true)) {
            return;
        }

        File dataFolder = TownsAndNations.getPlugin().getDataFolder();
        File archiveFile = new File(dataFolder, "history.txt");

        if (!archiveFile.exists()) {
            try {
                archiveFile.createNewFile();
            } catch (IOException e) {
                TownsAndNations.getPlugin().getLogger().severe("Could not create history file!");
                return;
            }
        }

        try (FileWriter fw = new FileWriter(archiveFile, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write( "[" + LocalDate.now() + "] " + lineToAdd);
            bw.newLine();
        } catch (IOException e) {
            TownsAndNations.getPlugin().getLogger().severe("Could not create history file!");
        }
    }
}