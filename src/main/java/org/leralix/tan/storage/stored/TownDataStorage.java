package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.territory.cosmetic.ICustomIcon;
import org.leralix.tan.dataclass.territory.permission.RelationPermission;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.storage.typeadapter.EnumMapDeserializer;
import org.leralix.tan.storage.typeadapter.EnumMapKeyValueDeserializer;
import org.leralix.tan.storage.typeadapter.IconAdapter;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TownDataStorage {
    private static final String ERROR_MESSAGE = "Error while creating town storage";
    private static TownDataStorage instance;
    private LinkedHashMap<String, TownData> townDataMap = new LinkedHashMap<>();
    private int newTownId = 1;

    protected TownDataStorage() {
        loadStats();
    }

    public static synchronized TownDataStorage getInstance() {
        if (instance == null) {
            instance = new TownDataStorage();
        }
        return instance;
    }

    public TownData newTown(String townName, ITanPlayer tanPlayer){
        String townId = getNextTownID();

        TownData newTown = new TownData(townId, townName, tanPlayer);


        townDataMap.put(townId,newTown);
        saveStats();
        return newTown;
    }

    private @NotNull String getNextTownID() {
        String townId = "T"+newTownId;
        newTownId++;
        return townId;
    }

    public TownData newTown(String townName){
        String townId = getNextTownID();

        TownData newTown = new TownData(townId, townName);

        townDataMap.put(townId,newTown);
        saveStats();
        return newTown;
    }


    public void deleteTown(TownData townData) {
        townDataMap.remove(townData.getID());
        saveStats();
    }

    public Map<String, TownData> getTownMap() {
        return townDataMap;
    }


    public TownData get(ITanPlayer tanPlayer){
        return get(tanPlayer.getTownId());
    }

    public TownData get(Player player){
        return get(PlayerDataStorage.getInstance().get(player).getTownId());
    }

    public TownData get(String townId) {
        return townDataMap.get(townId);
    }


    public int getNumberOfTown() {
        return townDataMap.size();
    }

    private void loadStats() {
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Towns.json");
        if (!file.exists())
            return;

        Reader reader;
        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            TownsAndNations.getPlugin().getLogger().severe(ERROR_MESSAGE);
            return;
        }


        Gson gson = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<Map<ChunkPermissionType, RelationPermission>>() {}.getType(), new EnumMapKeyValueDeserializer<>(ChunkPermissionType.class, RelationPermission.class))
            .registerTypeAdapter(new TypeToken<Map<TownRelation, List<String>>>() {}.getType(),new EnumMapDeserializer<>(TownRelation.class, new TypeToken<List<String>>(){}.getType()))
            .registerTypeAdapter(new TypeToken<List<RelationPermission>>() {}.getType(),new EnumMapDeserializer<>(RelationPermission.class, new TypeToken<List<String>>(){}.getType()))
            .registerTypeAdapter(ICustomIcon.class, new IconAdapter())
            .create();

        Type type = new TypeToken<LinkedHashMap<String, TownData>>() {}.getType();

        townDataMap = gson.fromJson(reader, type);

        int id = 0;
        for (String cle : townDataMap.keySet()) {
            int newID = Integer.parseInt(cle.substring(1));
            if (newID > id)
                id = newID;
        }
        newTownId = id + 1;
    }


    public void saveStats() {

        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(ICustomIcon.class, new IconAdapter())
                .create();


        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Towns.json");

        if(file.getParentFile().mkdirs())


        try {
            if (!file.exists()){
                file.createNewFile();
            }

        } catch (IOException e) {
            TownsAndNations.getPlugin().getLogger().severe(ERROR_MESSAGE);
            return;
        }
        try (Writer writer = new FileWriter(file, false)) {
            gson.toJson(townDataMap, writer);
        } catch (IOException e) {
            TownsAndNations.getPlugin().getLogger().severe(ERROR_MESSAGE);
        }

    }



    public boolean isNameUsed(String townName){
        for (TownData town : townDataMap.values()){
            if(townName.equals(town.getName()))
                return true;
        }
        return false;
    }

    public Collection<TownData> getAll(){
        return getTownMap().values();
    }


}
