package org.tan.TownsAndNations.storage;

import com.google.gson.Gson;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.DataClass.PlayerData;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerDataStorage {

    private static ArrayList<PlayerData> stats = new ArrayList<PlayerData>();

    public static void createPlayerDataClass(Player p) {

        PlayerData stat = new PlayerData(p);
        stats.add(stat);
        saveStats();
    }

    public static void deleteStat(String uuid) {
        for (PlayerData stat : stats) {
            if (stat.getUuid().equalsIgnoreCase(uuid)) {
                stats.remove(stat);
                break;
            }
        }
        saveStats();
    }

    public static PlayerData getStat(String id){
        for (PlayerData stat : stats) {
            if (stat.getUuid().equalsIgnoreCase(id)) {
                return stat;
            }
        }
        return null;
    }

    public static PlayerData getStat(Player player){
        String id = player.getUniqueId().toString();
        for (PlayerData stat : stats) {
            if (stat.getUuid().equalsIgnoreCase(id)) {
                return stat;
            }
        }
        return null;
    }

    public static PlayerData getStatUsername(String username){
        for (PlayerData stat : stats) {
            if (stat.getName().equalsIgnoreCase(username)) {
                return stat;
            }
        }
        return null;
    }

    public static PlayerData updateStat(String uuid, PlayerData newStat) throws IOException {
        for (PlayerData stat : stats) {
            if (stat.getUuid().equalsIgnoreCase(uuid)) {
                stat.setName(newStat.getName());
                stat.setBalance(newStat.getBalance());
            }
        }
        saveStats();
        return null;
    }

    public static List<PlayerData> getStats(){
        return stats;
    }

    public static void loadStats(){

        Gson gson = new Gson();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TaNstats.json");
        if (file.exists()){
            Reader reader = null;
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
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TaNstats.json");
        file.getParentFile().mkdir();
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Writer writer = null;
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