package org.tan.towns_and_nations.utils;

import com.google.gson.Gson;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.TownsAndNations;
import org.tan.towns_and_nations.commands.PlayerData.PlayerDataClass;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class PlayerStatStorage {

    private static ArrayList<PlayerDataClass> stats = new ArrayList<PlayerDataClass>();

    public static PlayerDataClass createPlayerDataClass(Player p){

        PlayerDataClass stat = new PlayerDataClass(p);
        stats.add(stat);

        return stat;
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

    public static PlayerDataClass findStatUUID(String uuid){
        for (PlayerDataClass stat : stats) {
            if (stat.getUuid().equalsIgnoreCase(uuid)) {
                return stat;
            }
        }
        return null;
    }

    public static PlayerDataClass findStatUsername(String username){
        for (PlayerDataClass stat : stats) {
            if (stat.getPlayerName().equalsIgnoreCase(username)) {
                return stat;
            }
        }
        return null;
    }


    public static PlayerDataClass updateStat(String uuid, PlayerDataClass newStat){
        for (PlayerDataClass stat : stats) {
            if (stat.getUuid().equalsIgnoreCase(uuid)) {
                stat.setPlayer(newStat.getPlayer());
                stat.setPlayerName(newStat.getPlayerName());
                stat.setBalance(newStat.getBalance());
            }
        }
        return null;
    }

    // Ajouter une fonction pour modifier une seule valeure

    public static ArrayList<PlayerDataClass> getStats(){
        return stats;
    }

    public static void loadStats() throws IOException {

        Gson gson = new Gson();
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