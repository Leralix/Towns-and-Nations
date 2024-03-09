package org.tan.TownsAndNations.storage.DataStorage;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.RegionData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.PlayerChatListenerStorage;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.ConfigUtil;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class RegionDataStorage {

    private static int nextID = 1;
    private static LinkedHashMap<String, RegionData> regionStorage = new LinkedHashMap<>();


    public static void createNewRegion(Player player, String regionName){

        TownData town = TownDataStorage.get(player);

        if(!town.isLeader(player)){
            player.sendMessage(getTANString() + Lang.PLAYER_ONLY_LEADER_CAN_PERFORM_ACTION.get());
            return;
        }

        int cost = ConfigUtil.getCustomConfig("config.yml").getInt("regionCost");
        if(town.getBalance() < cost){
            player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.get());
            return;
        }

        int maxSize = ConfigUtil.getCustomConfig("config.yml").getInt("RegionNameSize");
        if(regionName.length() > maxSize){
            player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            return;
        }

        Bukkit.broadcastMessage(ChatUtils.getTANString() + Lang.REGION_CREATE_SUCCESS_BROADCAST.get(town.getName(),regionName));
        PlayerChatListenerStorage.removePlayer(player);

        String regionID = "R"+nextID;
        String playerID = player.getUniqueId().toString();
        nextID++;

        RegionData newRegion = new RegionData(regionID, regionName, playerID);
        regionStorage.put(regionID, newRegion);
        town.setRegion(newRegion);
        town.removeToBalance(cost);
    }
    public static RegionData get(Player player){
        return get(PlayerDataStorage.get(player).getTown().getRegionID());
    }
    public static RegionData get(String regionID){
        if(!regionStorage.containsKey(regionID))
            return null;
        return regionStorage.get(regionID);
    }

    public static int getNumberOfRegion(){
        return regionStorage.size();
    }

    public static void removeRegion(String regionID){
        regionStorage.remove(regionID);
    }

    public static LinkedHashMap<String, RegionData> getRegionStorage(){
        return regionStorage;
    }

    public static Collection<RegionData> getAllRegions(){
        return regionStorage.values();
    }

    public static void deleteRegion(RegionData region){
        deleteRegion(region.getID());
    }
    public static void deleteRegion(String regionID){
        get(regionID).getCapital().addToBalance(7500);
        NewClaimedChunkStorage.unclaimAllChunkFromID(regionID);
        removeTownFromRegion(regionID);
        regionStorage.remove(regionID);
    }

    private static void removeTownFromRegion(String regionID) {
        RegionData region = get(regionID);

        for (String townID : region.getTownsID()){
            TownData town = TownDataStorage.get(townID);
            town.removeRegion();
        }
    }

    public static void loadStats() {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();

        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Regions.json");
        if (file.exists()){
            Reader reader = null;
            try {
                reader = new FileReader(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            Type type = new TypeToken<LinkedHashMap<String, RegionData>>() {}.getType();
            regionStorage = gson.fromJson(reader, type);

            int ID = 0;
            for (Map.Entry<String, RegionData> entry : regionStorage.entrySet()) {
                String cle = entry.getKey();
                int newID =  Integer.parseInt(cle.substring(1));
                if(newID > ID)
                    ID = newID;
            }
            nextID = ID+1;
        }

    }

    public static void saveStats() {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Regions.json");
        file.getParentFile().mkdirs();
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (Writer writer = new FileWriter(file, false)) {
            gson.toJson(regionStorage, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }



}