package org.leralix.tan.storage.stored.json;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class JsonStorage<T> {

    protected final File file;
    protected Map<String, T> dataMap;
    protected final Gson gson;
    private final Type type;
    private static final String NEW_FILE_FOLDER = "storage/json";


    protected JsonStorage(String fileName, Type type, Gson gson) {
        this.file = getFile(fileName);

        this.type = type;
        this.gson = gson;

        loadDatamap();
    }

    public void loadDatamap() {
        if (!file.exists()) {
            dataMap = new LinkedHashMap<>();
        }
        else {
            try (Reader reader = new FileReader(file)) {
                dataMap = gson.fromJson(reader, type);
            } catch (IOException e) {
                TownsAndNations.getPlugin().getLogger().severe("Error loading " + file.getName());
            }
        }
    }

    static @NotNull File getFile(String fileName) {
        File pluginFolder = TownsAndNations.getPlugin().getDataFolder();

        // Old path
        File oldFile = new File(pluginFolder, fileName);

        // New Path
        File newFolder = new File(pluginFolder, NEW_FILE_FOLDER);
        File newFile = new File(newFolder, fileName);

        // Use to migrate to the new destination
        if (oldFile.exists() && !newFile.exists()) {
            try {
                Files.createDirectories(newFolder.toPath()); // Création des dossiers nécessaires
                Files.move(
                        oldFile.toPath(),
                        newFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );
                Bukkit.getLogger().info(
                        "Moved " + fileName + " to new storage location: " + newFile.getAbsolutePath()
                );
            } catch (IOException e) {
                Bukkit.getLogger().severe(
                        "Failed to move " + fileName + " to new storage location. : " + e.getMessage()
                );
            }
        }

        return newFile;
    }

    public void save() {
        // Copy the map to avoid concurrent modification
        Map<String, T> snapshot = new LinkedHashMap<>(dataMap);
        CompletableFuture.runAsync(() -> saveSnapshot(snapshot));
    }

    public Map<String, T> getAll() {
        return dataMap;
    }

    public T get(String id) {
        return dataMap.get(id);
    }

    public void delete(String id) {
        dataMap.remove(id);
    }

    public void put(String id, T obj) {
        dataMap.put(id, obj);
    }

    private void saveSnapshot(Map<String, T> snapshot) {

        try {
            Files.createDirectories(file.getParentFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File tempFile = new File(file.getParent(), file.getName() + ".tmp");

        try (Writer writer = new FileWriter(tempFile, false)) {
            gson.toJson(snapshot, type, writer);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Error saving " + file.getName() + " : " + e.getMessage());
            return;
        }

        try {
            Files.move(
                    tempFile.toPath(),
                    file.toPath(),
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
            );
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to replace old file with new one: " + file.getName());
        }
    }

    protected int getNextID() {
        int id = 0;
        for (String cle : getAll().keySet()) {
            if (cle != null && cle.length() >= 2) {
                String suffix = cle.substring(1);
                boolean isNumeric = suffix.chars().allMatch(Character::isDigit);
                if (isNumeric) {
                    int newID = Integer.parseInt(suffix);
                    if (newID >= id) {
                        id = newID + 1;
                    }
                }
            }
        }
        return id;
    }
}
