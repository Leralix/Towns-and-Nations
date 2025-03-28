package org.leralix.tan.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.lang.Lang;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

        Collection<File> fileCollections = new ArrayList<>(getAllStorageFiles());

        archiveFiles(fileCollections, "archive", dateStr);
    }

    /**
     * Main method to archive files.
     * Every data file will be copied and stored in a zip
     * file with the current date as the name.
     */
    public static void archiveFiles(Collection<File> filesToArchive, String archiveFolderPath, String name) {
        File dataFolder = TownsAndNations.getPlugin().getDataFolder();
        File archiveFolder = new File(dataFolder, archiveFolderPath);
        if (!archiveFolder.exists()) {
            archiveFolder.mkdirs();
        }

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
            for(File file : filesToArchive){
                if(file.exists()){
                    addFileToArchive(file, zipOutputStream);
                }
            }
        } catch (IOException e) {
            TownsAndNations.getPlugin().getLogger().severe("Error while archiving files : " + e.getMessage());
        }
    }

    private static void addFileToArchive(File file, ZipOutputStream zipOutputStream) {
        try {
            addFileToZip(zipOutputStream, file);
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

    private static Collection<File> getAllStorageFiles(){
        Collection<File> fileCollections = new ArrayList<>();

        String dataFolder = TownsAndNations.getPlugin().getDataFolder().getAbsolutePath();
        fileCollections.add(new File(dataFolder, "TAN - Claimed Chunks.json"));
        fileCollections.add(new File(dataFolder, "TAN - Landmarks.json"));
        fileCollections.add(new File(dataFolder, "TAN - Newsletter.json"));
        fileCollections.add(new File(dataFolder, "TAN - Planned_wars.json"));
        fileCollections.add(new File(dataFolder, "TAN - Players.json"));
        fileCollections.add(new File(dataFolder, "TAN - Towns.json"));
        fileCollections.add(new File(dataFolder, "TAN - Regions.json"));

        return fileCollections;
    }

    public static void sendReport(CommandSender commandSender) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        String reportName = "Report_of_" + commandSender.getName() + "_at_" + dtf.format(now);

        Collection<File> fileCollections = new ArrayList<>(getAllStorageFiles());

        fileCollections.add(new File(TownsAndNations.getPlugin().getDataFolder(), "main.yml"));
        fileCollections.add(new File(TownsAndNations.getPlugin().getDataFolder(), "townUpgrades.yml"));

        File pluginListFile = getPluginNameFile();
        fileCollections.add(pluginListFile);

        ArchiveUtil.archiveFiles(fileCollections, "reports", reportName);
        commandSender.sendMessage(Lang.DEBUG_REPORT_CREATED.get());

        pluginListFile.delete();
    }

    private static @NotNull File getPluginNameFile() {
        File pluginListFile = new File(TownsAndNations.getPlugin().getDataFolder(), "plugin_list.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pluginListFile))) {
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                writer.write(plugin.getName() + " - " + plugin.getDescription().getVersion());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pluginListFile;
    }
}
