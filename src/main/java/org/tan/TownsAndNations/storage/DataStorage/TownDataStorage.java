package org.tan.TownsAndNations.storage.DataStorage;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.google.gson.internal.bind.DateTypeAdapter;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.*;
import org.tan.TownsAndNations.DataClass.territoryData.RegionData;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.enums.TownChunkPermission;
import org.tan.TownsAndNations.enums.ChunkPermissionType;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.enums.TownRolePermission;
import org.tan.TownsAndNations.utils.ConfigUtil;

import java.io.*;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;
import java.util.Date;

import static org.tan.TownsAndNations.TownsAndNations.isSQLEnabled;
import static org.tan.TownsAndNations.utils.TeamUtils.updateAllScoreboardColor;

public class TownDataStorage {

    private static LinkedHashMap<String, TownData> townDataMap = new LinkedHashMap<>();
    private static int newTownId = 1;
    private static Connection connection;

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


    public static void removeTown(String townID) {
        TownData townToDelete = get(townID);

        NewClaimedChunkStorage.unclaimAllChunkFromTown(townToDelete); //Unclaim all chunk from town

        RegionData region = RegionDataStorage.get(townToDelete.getRegionID());
        if(region != null)
            region.removeSubject(townToDelete);


        townToDelete.getRelations().cleanAll(townID);   //Cancel all Relation between the deleted town and other town
        townToDelete.removeALlLandmark(); //Remove all Landmark from the deleted town
        for(String playerID : townToDelete.getPlayerIDList()){ //Kick all Players from the deleted town
            townToDelete.removePlayer(PlayerDataStorage.get(playerID));
        }

        if(isSQLEnabled()) { //if SQL is enabled, some data need to be removed manually
            NewClaimedChunkStorage.unclaimAllChunkFromTown(townToDelete);  //Unclaim all chunk from the deleted town NOT WORKING RN
        }

        updateAllScoreboardColor();
        townDataMap.remove(townID);
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
        if(ConfigUtil.getCustomConfig("config.yml").getBoolean("AllowNameDuplication",true))
            return false;
        
        for (TownData town : townDataMap.values()){
            if(townName.equals(town.getName()))
                return true;
        }
        return false;
    }

}
