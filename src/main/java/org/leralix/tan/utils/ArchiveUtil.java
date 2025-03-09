package org.leralix.tan.utils;

import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This class is used to archive local data every date to avoid data loss.
 * Archived files are stored in the archive folder.
 */
public class ArchiveUtil {
    private ArchiveUtil() {
        throw new IllegalStateException("Utility class");
    }


    public static void archiveFiles(){
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        archiveFiles("archive", dateStr);
    }

    /**
     * Main method to archive files.
     * Every data file will be copied and stored in a zip
     * file with the current date as the name.
     */
    public static void archiveFiles(String archiveFolderChild, String name) {
        File DataFolder = TownsAndNations.getPlugin().getDataFolder();
        File archiveFolder = new File(DataFolder, archiveFolderChild);
        if (!archiveFolder.exists()) {
            archiveFolder.mkdirs();
        }


        File file1 = new File(DataFolder, "TAN - Claimed Chunks.json");
        File file2 = new File(DataFolder, "TAN - Players.json");
        File file3 = new File(DataFolder, "TAN - Towns.json");
        File file4 = new File(DataFolder, "TAN - Regions.json");

        File zipFile;
        int counter = 0;
        while (true) {
            String suffix = counter == 0 ? "" : " - " + counter;
            zipFile = new File(archiveFolder, name + suffix + ".zip");
            if (!zipFile.exists()) {
                break;
            }
            counter++;
        }

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
            addFileToZip(zipOutputStream, file1);
            addFileToZip(zipOutputStream, file2);
            addFileToZip(zipOutputStream, file3);
            addFileToZip(zipOutputStream, file4);
        } catch (IOException e) {
            TownsAndNations.getPlugin().getLogger().severe("Error while archiving files : " + e.getMessage());
        }
    }

    /**
     * Add a single file to the daily Zip File.
     * @param zipOutputStream   the zip file to add the file to
     * @param file              the file to add
     * @throws IOException      if an error occurs while reading the file
     */
    private static void addFileToZip(final @NotNull ZipOutputStream zipOutputStream, final @NotNull File file) throws IOException {
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
