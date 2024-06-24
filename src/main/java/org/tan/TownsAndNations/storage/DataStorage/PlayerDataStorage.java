package org.tan.TownsAndNations.storage.DataStorage;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.DataClass.PlayerData;

import java.io.*;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

public class PlayerDataStorage {

    private static ArrayList<PlayerData> stats = new ArrayList<>();
    private static Map<String, PlayerData> playerStorage = new HashMap<>();

    private static Connection connection;

    public static PlayerData createPlayerDataClass(Player p) {
        PlayerData playerData = new PlayerData(p);
        return createPlayerDataClass(playerData);
    }
    public static PlayerData createPlayerDataClass(PlayerData p) {
        playerStorage.put(p.getID(), p);
        saveStats();
        return p;
    }

    public static void deleteData(String playerID) {
        playerStorage.remove(playerID);
        saveStats();
    }

    public static PlayerData get(OfflinePlayer player) {
        return get(player.getUniqueId().toString());
    }
    public static PlayerData get(Player player) {
        return get(player.getUniqueId().toString());
    }
    public static PlayerData get(UUID player) {
        return get(player.toString());
    }
    public static PlayerData get(String id){

        if(id == null)
            return null;

        PlayerData res = playerStorage.get(id);
        if(res != null)
            return res;

        Player newPlayer = TownsAndNations.getPlugin().getServer().getPlayer(UUID.fromString(id));
        if(newPlayer != null){
            return createPlayerDataClass(newPlayer);
        }
        return null;
    }



    public static Collection<PlayerData> getLists() {
        return playerStorage.values();
    }
    public static Collection<PlayerData> getOldLists() {
        return stats;
    }

    public static void loadOldStats(){

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Stats.json");
        if (file.exists()){
            Reader reader;
            try {
                reader = new FileReader(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            PlayerData[] n = gson.fromJson(reader, PlayerData[].class);
            stats = new ArrayList<>(Arrays.asList(n));

        }

    }
    public static void saveOldStats() {

        Gson gson = new Gson();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Stats.json");
        file.getParentFile().mkdir();

        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Writer writer;
        try {
            writer = new FileWriter(file, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gson.toJson(stats, writer);
        try {
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public static void loadStats(){

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Players.json");
        if (file.exists()){
            Reader reader;
            try {
                reader = new FileReader(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            Type type = new TypeToken<HashMap<String, PlayerData>>() {}.getType();
            playerStorage = gson.fromJson(reader, type);

        }

    }
    public static void saveStats() {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Players.json");
        file.getParentFile().mkdir();

        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Writer writer;
        try {
            writer = new FileWriter(file, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gson.toJson(playerStorage, writer);
        try {
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}