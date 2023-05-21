package org.tan.towns_and_nations.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.TownsAndNations;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class TownDataStorage {

    public static HashMap<String, TownDataClass> townDataMap = new HashMap<>();
    public static int newTownId = 1;

    public static void newTown(String townName, Player leader){
        String townId = "T"+newTownId;
        TownDataClass newTown = new TownDataClass( townId, townName, leader.getUniqueId().toString());
        PlayerStatStorage.findStatUUID(leader.getUniqueId().toString()).setTownId(townId);
        townDataMap.put(townId,newTown);

        saveStats();
        newTownId = newTownId+1;

    }

    public static void removeTown(int TownId){
        townDataMap.remove(TownId);
        saveStats();
    }

    public static HashMap<String, TownDataClass> getTownList(){
        return townDataMap;
    }

    public static TownDataClass getTown(String townId){
        return townDataMap.get(townId);
    }


    public static void loadStats() {

        Gson gson = new Gson();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TaNTownsStats.json");
        if (file.exists()){
            Reader reader = null;
            try {
                reader = new FileReader(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            Type type = new TypeToken<HashMap<String, TownDataClass>>() {}.getType();
            townDataMap = gson.fromJson(reader, type);
            System.out.println("[TaN]Stats Loaded");

        }

    }

    public static void saveStats() {

        Gson gson = new Gson();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TaNTownsStats.json");
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
        gson.toJson(townDataMap, writer);
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
        System.out.println("[TaN]Stats saved");

    }


}
