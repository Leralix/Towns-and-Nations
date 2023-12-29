package org.tan.TownsAndNations.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.DataClass.PlayerData;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerDataStorage {

    private static ArrayList<PlayerData> stats = new ArrayList<>();

    public static PlayerData createPlayerDataClass(Player p) {

        PlayerData stat = new PlayerData(p);
        stats.add(stat);
        saveStats();
        return stat;
    }



    public static void deleteData(String uuid) {

        for (PlayerData stat : stats) {
            if (stat.getUuid().equalsIgnoreCase(uuid)) {
                stats.remove(stat);
                break;
            }
        }
        saveStats();
    }

    public static PlayerData get(OfflinePlayer player) {
        return get(player.getUniqueId().toString());
    }
    public static PlayerData get(Player player) {
        return get(player.getUniqueId().toString());
    }

    public static PlayerData get(String id){
        for (PlayerData stat : stats) {
            if (stat.getUuid().equalsIgnoreCase(id)) {
                return stat;
            }
        }
        return null;
    }



    public static List<PlayerData> getStats(){
        return stats;
    }

    public static void loadStats(){

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

    public static void saveStats() {

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

}