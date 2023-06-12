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

    public static LinkedHashMap<String, TownDataClass> townDataMap = new LinkedHashMap<>();

    public static int newTownId = 1;
    public static void newTown(String townName, Player leader){
        String townId = "T"+newTownId;
        TownDataClass newTown = new TownDataClass( townId, townName, leader.getUniqueId().toString());
        PlayerStatStorage.findStatUUID(leader.getUniqueId().toString()).setTownId(townId);
        townDataMap.put(townId,newTown);

        saveStats();
        newTownId = newTownId+1;

    }
    public static void removeTown(String TownId){

        TownDataClass townDataClass = townDataMap.get(TownId);

         ArrayList<String> array = townDataClass.getPlayerList();
            for(String playerUUID : array) {
                PlayerStatStorage.findStatUUID(playerUUID).setTownId(null);
            }

        townDataMap.remove(TownId);
        saveStats();
    }
    public static LinkedHashMap<String, TownDataClass> getTownList(){
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
            System.out.println("[TaN]Town Stats Loaded");
            System.out.println("[TaN]First Free Number in town ID:" + ID);

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
