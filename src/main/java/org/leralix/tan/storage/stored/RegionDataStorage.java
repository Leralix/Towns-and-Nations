package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.tan.storage.typeadapter.EnumMapDeserializer;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.utils.FileUtil;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.storage.typeadapter.IconAdapter;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.cosmetic.CustomIcon;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class RegionDataStorage {

    private static int nextID = 1;
    private static LinkedHashMap<String, RegionData> regionStorage = new LinkedHashMap<>();


    public static void createNewRegion(Player player, String regionName){

        TownData town = TownDataStorage.get(player);

        if(!town.isLeader(player)){
            player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_ONLY_LEADER_CAN_PERFORM_ACTION.get());
            return;
        }

        int cost = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("regionCost");
        if(town.getBalance() < cost){
            player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.get());
            return;
        }

        int maxSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("RegionNameSize");
        if(regionName.length() > maxSize){
            player.sendMessage(TanChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            return;
        }

        if(isNameUsed(regionName)){
            player.sendMessage(TanChatUtils.getTANString() + Lang.NAME_ALREADY_USED.get());
            return;
        }

        Bukkit.broadcastMessage(TanChatUtils.getTANString() + Lang.REGION_CREATE_SUCCESS_BROADCAST.get(town.getName(),regionName));
        PlayerChatListenerStorage.removePlayer(player);

        String regionID = "R"+nextID;
        String playerID = player.getUniqueId().toString();
        nextID++;

        RegionData newRegion = new RegionData(regionID, regionName, playerID);
        regionStorage.put(regionID, newRegion);

        town.setOverlord(newRegion);
        town.removeFromBalance(cost);
        FileUtil.addLineToHistory(Lang.HISTORY_REGION_CREATED.get(player.getName(),regionName));
    }
    public static RegionData get(Player player){
        return get(PlayerDataStorage.get(player));
    }
    public static RegionData get(PlayerData playerData){
        TownData town = TownDataStorage.get(playerData);
        if(town == null)
            return null;
        return town.getRegion();
    }
    public static RegionData get(String regionID){
        if(!regionStorage.containsKey(regionID))
            return null;
        return regionStorage.get(regionID);
    }

    public static int getNumberOfRegion(){
        return regionStorage.size();
    }


    public static Map<String, RegionData> getRegionStorage(){
        return regionStorage;
    }

    public static Collection<RegionData> getAll(){
        return regionStorage.values();
    }

    public static void deleteRegion(RegionData region){
        regionStorage.remove(region.getID());
        saveStats();
    }

    public static boolean isNameUsed(String name){
        if(ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("AllowNameDuplication",true))
            return false;

        for (RegionData region : regionStorage.values()){
            if(region.getName().equalsIgnoreCase(name))
                return true;
        }
        return false;
    }

    public static void loadStats() {

        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Regions.json");
        if (!file.exists()){
            return;
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<Map<TownRelation, List<String>>>() {}.getType(),new EnumMapDeserializer<>(TownRelation.class, new TypeToken<List<String>>(){}.getType()))
                .registerTypeAdapter(CustomIcon.class, new IconAdapter())
                .create();

        Reader reader;
        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Type type = new TypeToken<LinkedHashMap<String, RegionData>>() {}.getType();
        regionStorage = gson.fromJson(reader, type);

        int id = 0;
        for (Map.Entry<String, RegionData> entry : regionStorage.entrySet()) {
            String cle = entry.getKey();
            int newID =  Integer.parseInt(cle.substring(1));
            if(newID > id)
                id = newID;
        }
        nextID = id+1;
    }

    public static void saveStats() {

        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(CustomIcon.class, new IconAdapter())
                .create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Regions.json");
        file.getParentFile().mkdirs();
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Writer writer;
        try {
            writer = new FileWriter(file, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gson.toJson(regionStorage, writer);

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
    }



}
