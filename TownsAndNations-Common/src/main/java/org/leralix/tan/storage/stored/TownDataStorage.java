package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.property.AbstractOwner;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.territory.cosmetic.ICustomIcon;
import org.leralix.tan.dataclass.territory.permission.RelationPermission;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.storage.typeadapter.EnumMapDeserializer;
import org.leralix.tan.storage.typeadapter.EnumMapKeyValueDeserializer;
import org.leralix.tan.storage.typeadapter.IconAdapter;
import org.leralix.tan.storage.typeadapter.OwnerDeserializer;

import java.util.ArrayList;
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
                        .registerTypeAdapter(AbstractOwner.class, new OwnerDeserializer())
                        .registerTypeAdapter(ICustomIcon.class, new IconAdapter())
                        .setPrettyPrinting()
                        .create());
    }

    @Override
    protected void load() {
        super.load();

        int id = 0;
        for (String cle : dataMap.keySet()) {
            if (cle == null || cle.length() < 2) {
                continue;
            }

            String suffix = cle.substring(1);
            boolean isNumeric = suffix.chars().allMatch(Character::isDigit);
            if (!isNumeric) {
                continue;
            }

            int newID = Integer.parseInt(suffix);
            if (newID > id) {
                id = newID;
            }
        }
        newTownId = id + 1;
    }

    @Override
    public void reset() {
        instance = null;
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
        String townId = "T"+ newTownId;
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

    public void checkValidWorlds() {
        for (TownData town : new ArrayList<>(getAll().values())) {
            for (PropertyData property : town.getProperties()) {
                if (property.getPosition().getWorld() == null) {
                    property.delete();
                    TownsAndNations.getPlugin().getLogger().warning("Deleted property " + property.getName() + " due to invalid world.");
                }
            }
            var optCapital = town.getCapitalLocation();
            if (optCapital.isPresent() && optCapital.get().getWorld() == null) {
                town.setCapitalLocation(null);
                TownsAndNations.getPlugin().getLogger().warning("Removed capital location for town " + town.getName() + " due to invalid world.");
            }
        }
    }
}
