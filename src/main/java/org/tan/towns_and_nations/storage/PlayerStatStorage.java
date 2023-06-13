package org.tan.towns_and_nations.storage;

import com.google.gson.Gson;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.TownsAndNations;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class PlayerStatStorage {

    private static ArrayList<PlayerDataClass> stats = new ArrayList<PlayerDataClass>();

    public static void createPlayerDataClass(Player p) throws IOException {

        PlayerDataClass stat = new PlayerDataClass(p);
        stats.add(stat);
        saveStats();
    }

    public static void deleteStat(String uuid) throws IOException {
        for (PlayerDataClass stat : stats) {
            if (stat.getUuid().equalsIgnoreCase(uuid)) {
                stats.remove(stat);
                break;
            }
        }
        saveStats();
    }

    public static PlayerDataClass getStatUUID(String uuid){
        for (PlayerDataClass stat : stats) {
            if (stat.getUuid().equalsIgnoreCase(uuid)) {
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

    // Ajouter une fonction pour modifier une seule valeure

    public static ArrayList<PlayerDataClass> getStats(){
        return stats;
    }

    public static void loadStats() throws IOException {

        Gson gson = new Gson();
        System.out.println(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TaNstats.json");
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TaNstats.json");
        if (file.exists()){
            Reader reader = new FileReader(file);
            PlayerDataClass[] n = gson.fromJson(reader, PlayerDataClass[].class);
            stats = new ArrayList<>(Arrays.asList(n));
            System.out.println("[TaN]Stats Loaded");

        }

    }

    public static void saveStats() throws IOException {

        Gson gson = new Gson();
        System.out.println(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath());
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TaNstats.json");
        file.getParentFile().mkdir();
        file.createNewFile();
        Writer writer = new FileWriter(file, false);
        gson.toJson(stats, writer);
        writer.flush();
        writer.close();
        System.out.println("[TaN]Stats saved");

    }

}