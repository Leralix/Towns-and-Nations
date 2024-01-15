package org.tan.TownsAndNations.utils;

import org.tan.TownsAndNations.TownsAndNations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ArchiveUtil {

    public static void archiveFiles() {
        File DataFolder = TownsAndNations.getPlugin().getDataFolder();
        File archiveFolder = new File(DataFolder, "archive");
        if (!archiveFolder.exists()) {
            archiveFolder.mkdirs();
        }

        String dateStr = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        File file1 = new File(DataFolder, "TAN - Chunks.json");
        File file2 = new File(DataFolder, "TAN - Stats.json");
        File file3 = new File(DataFolder, "TAN - Towns.json");

        File zipFile;
        int counter = 0;
        while (true) {
            String suffix = counter == 0 ? "" : " - " + counter;
            zipFile = new File(archiveFolder, dateStr + suffix + ".zip");
            if (!zipFile.exists()) {
                break;
            }
            counter++;
        }

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
            addFileToZip(zipOutputStream, file1);
            addFileToZip(zipOutputStream, file2);
            addFileToZip(zipOutputStream, file3);

        } catch (IOException e) {
            TownsAndNations.getPluginLogger().severe("Error while archiving files : " + e.getMessage());
        }
    }



    private static void addFileToZip(ZipOutputStream zipOutputStream, File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zipOutputStream.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zipOutputStream.write(buffer, 0, length);
            }

            zipOutputStream.closeEntry();
        }
    }
}
