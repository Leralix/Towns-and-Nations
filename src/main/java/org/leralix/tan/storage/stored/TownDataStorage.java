package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.territory.cosmetic.ICustomIcon;
import org.leralix.tan.dataclass.territory.permission.RelationPermission;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.storage.typeadapter.EnumMapDeserializer;
import org.leralix.tan.storage.typeadapter.EnumMapKeyValueDeserializer;
import org.leralix.tan.storage.typeadapter.IconAdapter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TownDataStorage extends JsonStorage<TownData>{

    private static TownDataStorage instance;

    private int newTownId;

    private TownDataStorage() {
        super("TAN - Towns.json",
                new TypeToken<LinkedHashMap<String, TownData>>() {}.getType(),
                new GsonBuilder()
                        .registerTypeAdapter(new TypeToken<Map<ChunkPermissionType, RelationPermission>>() {}.getType(), new EnumMapKeyValueDeserializer<>(ChunkPermissionType.class, RelationPermission.class))
                        .registerTypeAdapter(new TypeToken<Map<TownRelation, List<String>>>() {}.getType(),new EnumMapDeserializer<>(TownRelation.class, new TypeToken<List<String>>(){}.getType()))
                        .registerTypeAdapter(new TypeToken<List<RelationPermission>>() {}.getType(),new EnumMapDeserializer<>(RelationPermission.class, new TypeToken<List<String>>(){}.getType()))
                        .registerTypeAdapter(ICustomIcon.class, new IconAdapter())
                        .setPrettyPrinting()
                        .create());
    }

    @Override
    protected void load() {
        super.load();

        int id = 0;
        for (String cle : dataMap.keySet()) {
            try {
                int newID = Integer.parseInt(cle.substring(1));
                if (newID > id) {
                    id = newID;
                }
            } catch (NumberFormatException ignored) {

            }
        }
        newTownId = id + 1;
        System.out.println("Next town ID will be : " + newTownId);
    }


    public static TownDataStorage getInstance() {
        if (instance == null)
            instance = new TownDataStorage();
        return instance;
    }

    public TownData newTown(String townName, ITanPlayer tanPlayer){
        String townId = getNextTownID();
        TownData newTown = new TownData(townId, townName, tanPlayer);

        put(townId,newTown);
        return newTown;
    }

    private @NotNull String getNextTownID() {
        System.out.println("getNextTownID : " + newTownId);
        String townId = "T"+newTownId;
        newTownId++;
        return townId;
    }

    public TownData newTown(String townName){
        String townId = getNextTownID();

        TownData newTown = new TownData(townId, townName);

        put(townId,newTown);
        return newTown;
    }


    public void deleteTown(TownData townData) {
        dataMap.remove(townData.getID());
        save();
    }


    public TownData get(ITanPlayer tanPlayer){
        return get(tanPlayer.getTownId());
    }

    public TownData get(Player player){
        return get(PlayerDataStorage.getInstance().get(player).getTownId());
    }


    public int getNumberOfTown() {
        return dataMap.size();
    }

    public boolean isNameUsed(String townName){
        for (TownData town : dataMap.values()){
            if(townName.equals(town.getName()))
                return true;
        }
        return false;
    }
}
