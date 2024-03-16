package org.tan.TownsAndNations.utils;

import org.tan.TownsAndNations.TownsAndNations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

public class FileUtil {
    public static void addLineToHistory(String lineToAdd) {

        if(!ConfigUtil.getCustomConfig("config.yml").getBoolean("archiveHistory",true)) {
            return;
        }

        File DataFolder = TownsAndNations.getPlugin().getDataFolder();
        File archiveFile = new File(DataFolder, "history.txt");

        if (!archiveFile.exists()) {
            try {
                archiveFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        try (FileWriter fw = new FileWriter(archiveFile, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write( "[" + LocalDate.now() + "] " + lineToAdd);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}