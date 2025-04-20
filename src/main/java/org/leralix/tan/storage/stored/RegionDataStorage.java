package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.cosmetic.ICustomIcon;
import org.leralix.tan.storage.typeadapter.EnumMapDeserializer;
import org.leralix.tan.utils.FileUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.storage.typeadapter.IconAdapter;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.lang.Lang;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class RegionDataStorage {

    private int nextID = 1;
    private LinkedHashMap<String, RegionData> regionStorage = new LinkedHashMap<>();
    private static RegionDataStorage instance;

    public static RegionDataStorage getInstance() {
        if(instance == null)
            instance = new RegionDataStorage();
        return instance;
    }

    private RegionDataStorage() {
        loadStats();
    }

    public RegionData createNewRegion(String name, TownData capital){

        PlayerData newLeader = capital.getLeaderData();

        String regionID = generateNextID();

        RegionData newRegion = new RegionData(regionID, name, newLeader.getID());
        regionStorage.put(regionID, newRegion);
        capital.setOverlord(newRegion);

        FileUtil.addLineToHistory(Lang.HISTORY_REGION_CREATED.get(newLeader.getNameStored(), name));
        return newRegion;
    }

    private @NotNull String generateNextID() {
        String regionID = "R"+nextID;
        nextID++;
        return regionID;
    }

    public RegionData get(Player player){
        return get(PlayerDataStorage.getInstance().get(player));
    }
    public RegionData get(PlayerData playerData){
        TownData town = TownDataStorage.getInstance().get(playerData);
        if(town == null)
            return null;
        return town.getRegion();
    }
    public RegionData get(String regionID){
        if(!regionStorage.containsKey(regionID))
            return null;
        return regionStorage.get(regionID);
    }

    public int getNumberOfRegion(){
        return regionStorage.size();
    }


    public Map<String, RegionData> getRegionStorage(){
        return regionStorage;
    }

    public Collection<RegionData> getAll(){
        return regionStorage.values();
    }

    public void deleteRegion(RegionData region){
        regionStorage.remove(region.getID());
        saveStats();
    }

    public boolean isNameUsed(String name){
        for (RegionData region : regionStorage.values()){
            if(region.getName().equalsIgnoreCase(name))
                return true;
        }
        return false;
    }

    public void loadStats() {

        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Regions.json");
        if (!file.exists()){
            return;
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<Map<TownRelation, List<String>>>() {}.getType(),new EnumMapDeserializer<>(TownRelation.class, new TypeToken<List<String>>(){}.getType()))
                .registerTypeAdapter(ICustomIcon.class, new IconAdapter())
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

    public void saveStats() {

        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(ICustomIcon.class, new IconAdapter())
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
