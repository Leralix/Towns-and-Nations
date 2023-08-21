package org.tan.TownsAndNations.storage;

import com.google.gson.Gson;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.DataClass.PlayerDataClass;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerStatStorage {

    private static ArrayList<PlayerDataClass> stats = new ArrayList<PlayerDataClass>();

    public static void createPlayerDataClass(Player p) {

        PlayerDataClass stat = new PlayerDataClass(p);
        stats.add(stat);
        saveStats();
    }

    public static void deleteStat(String uuid) {
        for (PlayerDataClass stat : stats) {
            if (stat.getUuid().equalsIgnoreCase(uuid)) {
                stats.remove(stat);
                break;
            }
        }
        saveStats();
    }

    public static PlayerDataClass getStat(String id){
        for (PlayerDataClass stat : stats) {
            if (stat.getUuid().equalsIgnoreCase(id)) {
                return stat;
            }
        }
        return null;
    }

    public static PlayerDataClass getStat(Player player){
        String id = player.getUniqueId().toString();
        for (PlayerDataClass stat : stats) {
            if (stat.getUuid().equalsIgnoreCase(id)) {
                return stat;
            }
        }
        return null;
    }

    public static PlayerDataClass getStatUsername(String username){
        for (PlayerDataClass stat : stats) {
            if (stat.getPlayerName().equalsIgnoreCase(username)) {
                return stat;
            }
        }
        return null;
    }

    public static PlayerDataClass updateStat(String uuid, PlayerDataClass newStat) throws IOException {
        for (PlayerDataClass stat : stats) {
            if (stat.getUuid().equalsIgnoreCase(uuid)) {
                stat.setPlayerName(newStat.getPlayerName());
                stat.setBalance(newStat.getBalance());
            }
        }
        saveStats();
        return null;
    }

    public static List<PlayerDataClass> getStats(){
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
            PlayerDataClass[] n = gson.fromJson(reader, PlayerDataClass[].class);
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