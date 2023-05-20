package org.tan.towns_and_nations.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.TownsAndNations;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class TownDataStorage {

    public static HashMap<Integer, TownDataClass> townDataMap = new HashMap<>();
    public static int nextTownId = -1;

    public static void newTown(String townName, String uuidLeader){

        if(nextTownId == -1){
            nextTownId = 0;
        }

        TownDataClass newTown = new TownDataClass( "T"+nextTownId, townName, uuidLeader);
        nextTownId = nextTownId+1;


        townDataMap.put(nextTownId,newTown);
        saveStats();
    }

    public static void removeTown(int TownId){
        townDataMap.remove(nextTownId);
    }

    public static HashMap<Integer, TownDataClass> getTownList(){
        return townDataMap;
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
            Type type = new TypeToken<HashMap<Integer, PlayerDataClass>>() {}.getType();
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
