package org.tan.TownsAndNations.storage;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.TownsAndNations;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class TownDataStorage {

    private static LinkedHashMap<String, TownData> townDataMap = new LinkedHashMap<>();
    private static int newTownId = 1;

    public static TownData newTown(String townName, Player leader){
        String townId = "T"+newTownId;
        TownData newTown = new TownData( townId, townName, leader.getUniqueId().toString());
        townDataMap.put(townId,newTown);

        saveStats();
        newTownId = newTownId+1;
        return newTown;
    }
    public static void removeTown(String TownId){

        TownData townData = townDataMap.get(TownId);

        HashSet<String> array = townData.getPlayerList();
        for(String playerUUID : array) {
            PlayerDataStorage.get(playerUUID).setTownId(null);
        }

        townDataMap.remove(TownId);
        saveStats();
    }
    public static void removeTown(TownData townData){
        removeTown(townData.getID());
    }
    public static LinkedHashMap<String, TownData> getTownList(){
        return townDataMap;
    }
    public static TownData get(String townId){
        return townDataMap.get(townId);
    }
    public static TownData get(PlayerData playerData){
        return townDataMap.get(playerData.getTownId());
    }
    public static TownData get(Player player){
        return townDataMap.get(PlayerDataStorage.get(player.getUniqueId().toString()).getTownId());
    }
    public static int getNumberOfTown(){
        return townDataMap.size();
    }
    public static void loadStats() {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();

        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Towns.json");
        if (file.exists()){
            Reader reader = null;
            try {
                reader = new FileReader(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            Type type = new TypeToken<LinkedHashMap<String, TownData>>() {}.getType();
            townDataMap = gson.fromJson(reader, type);

            int ID = 0;
            for (Map.Entry<String, TownData> entry : townDataMap.entrySet()) {
                String cle = entry.getKey();
                int newID =  Integer.parseInt(cle.substring(1));
                if(newID > ID)
                    ID = newID;
            }
            newTownId = ID+1;

        }

    }

    public static void saveStats() {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Towns.json");
        file.getParentFile().mkdirs();
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (Writer writer = new FileWriter(file, false)) {
            gson.toJson(townDataMap, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public static int getNewTownId() {
        return newTownId;
    }

}
