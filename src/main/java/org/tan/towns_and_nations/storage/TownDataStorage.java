package org.tan.towns_and_nations.storage;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.TownsAndNations;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class TownDataStorage {

    public static LinkedHashMap<String, TownDataClass> townDataMap = new LinkedHashMap<>();

    public static int newTownId = 1;
    public static void newTown(String townName, Player leader){
        String townId = "T"+newTownId;
        TownDataClass newTown = new TownDataClass( townId, townName, leader.getUniqueId().toString());
        PlayerStatStorage.getStat(leader.getUniqueId().toString()).setTownId(townId);
        townDataMap.put(townId,newTown);

        saveStats();
        newTownId = newTownId+1;

    }
    public static void removeTown(String TownId){

        TownDataClass townDataClass = townDataMap.get(TownId);

        ArrayList<String> array = townDataClass.getPlayerList();
        for(String playerUUID : array) {
            PlayerStatStorage.getStat(playerUUID).setTownId(null);
        }

        townDataMap.remove(TownId);
        saveStats();
    }
    public static void removeTown(TownDataClass townData){
        removeTown(townData.getTownId());
    }
    public static LinkedHashMap<String, TownDataClass> getTownList(){
        return townDataMap;
    }
    public static TownDataClass getTown(String townId){
        return townDataMap.get(townId);
    }
    public static TownDataClass getTown(PlayerDataClass playerDataClass){
        return townDataMap.get(playerDataClass.getTownId());
    }
    public static TownDataClass getTown(Player player){
        return townDataMap.get(PlayerStatStorage.getStat(player.getUniqueId().toString()).getTownId());
    }
    public static int getNumberOfTown(){
        return townDataMap.size();
    }
    public static void loadStats() {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();

        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TaNTownsStats.json");
        if (file.exists()){
            Reader reader = null;
            try {
                reader = new FileReader(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            Type type = new TypeToken<LinkedHashMap<String, TownDataClass>>() {}.getType();
            townDataMap = gson.fromJson(reader, type);

            int ID = 0;
            for (Map.Entry<String, TownDataClass> entry : townDataMap.entrySet()) {
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
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TaNTownsStats.json");
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


}
