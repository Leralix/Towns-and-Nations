package org.leralix.tan.utils.file;

import org.leralix.tan.TownsAndNations;
import org.leralix.tan.lang.FilledLang;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

/**
 * This static class manage the archive txt for admins.
 */
public class FileUtil {
    private static final String ERROR_MESSAGE = "Could not create history file!";

    private static boolean enable = false;

    private FileUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void setEnable(boolean value){
        enable = value;
    }

    public static void addLineToHistory(final FilledLang lineToAdd) {

        if (!enable) {
            return;
        }

        File dataFolder = TownsAndNations.getPlugin().getDataFolder();
        File archiveFile = new File(dataFolder, "history.txt");

        if (!archiveFile.exists()) {
            try {
                if (!archiveFile.createNewFile()) {
                    TownsAndNations.getPlugin().getLogger().severe(ERROR_MESSAGE);
                }
            } catch (IOException e) {
                TownsAndNations.getPlugin().getLogger().severe(ERROR_MESSAGE);
                return;
            }
        }

        try (FileWriter fw = new FileWriter(archiveFile, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("[" + LocalDate.now() + "] " + lineToAdd.getDefault());
            bw.newLine();
        } catch (IOException e) {
            TownsAndNations.getPlugin().getLogger().severe(ERROR_MESSAGE);
        }
    }
}