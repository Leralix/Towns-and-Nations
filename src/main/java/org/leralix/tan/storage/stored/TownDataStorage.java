package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.google.gson.internal.bind.DateTypeAdapter;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.*;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Date;

public class TownDataStorage {

    private static LinkedHashMap<String, TownData> townDataMap = new LinkedHashMap<>();
    private static int newTownId = 1;

    public static TownData newTown(String townName, Player player){
        String townId = "T"+newTownId;
        String playerID = player.getUniqueId().toString();
        newTownId++;

        TownData newTown = new TownData(townId, townName, playerID);


        townDataMap.put(townId,newTown);
        saveStats();
        return newTown;
    }

    public static TownData newTown(String townName){
        String townId = "T"+newTownId;
        newTownId++;

        TownData newTown = new TownData(townId, townName, null);

        townDataMap.put(townId,newTown);
        saveStats();
        return newTown;
    }


    public static void deleteTown(TownData townData) {
        townDataMap.remove(townData.getID());
        saveStats();
    }

    public static LinkedHashMap<String, TownData> getTownMap() {
        return townDataMap;
    }


    public static TownData get(PlayerData playerData){
        return get(playerData.getTownId());
    }
    public static TownData get(Player player){
        return get(PlayerDataStorage.get(player).getTownId());
    }
    public static TownData get(String townId) {
        return townDataMap.get(townId);
    }


    public static int getNumberOfTown() {
        return townDataMap.size();
    }

    public static void loadStats() {

        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Towns.json");
        if (!file.exists())
            return;

        Reader reader;
        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .registerTypeAdapter(Map.class, (JsonDeserializer<Map<String, Object>>) (json1, typeOfT, context) -> new Gson().fromJson(json1, typeOfT))
                .create();

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



    public static boolean isNameUsed(String townName){
        if(ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("AllowNameDuplication",true))
            return false;
        
        for (TownData town : townDataMap.values()){
            if(townName.equals(town.getName()))
                return true;
        }
        return false;
    }

}
