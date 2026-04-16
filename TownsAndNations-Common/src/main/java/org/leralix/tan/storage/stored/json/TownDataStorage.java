package org.leralix.tan.storage.stored.json;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.leralix.tan.data.building.property.owner.AbstractOwner;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.data.territory.cosmetic.ICustomIcon;
import org.leralix.tan.data.territory.permission.ChunkPermissionType;
import org.leralix.tan.data.territory.permission.RelationPermission;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.storage.stored.TownStorage;
import org.leralix.tan.storage.typeadapter.EnumMapDeserializer;
import org.leralix.tan.storage.typeadapter.EnumMapKeyValueDeserializer;
import org.leralix.tan.storage.typeadapter.IconAdapter;
import org.leralix.tan.storage.typeadapter.OwnerDeserializer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TownDataStorage extends JsonStorage<Town> implements TownStorage {

    private int nextID;

    public TownDataStorage() {
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
        this.nextID = getNextID();
    }

    private int getNextID() {
        int id = 0;
        for (String cle : getAll().keySet()) {
            if (cle != null && cle.length() >= 2) {
                String suffix = cle.substring(1);
                boolean isNumeric = suffix.chars().allMatch(Character::isDigit);
                if (isNumeric) {
                    int newID = Integer.parseInt(suffix);
                    if (newID > id) {
                        id = newID + 1;
                    }
                }
            }
        }
        return id;
    }

    @Override
    public Town newTown(String townName, @Nullable ITanPlayer tanPlayer){
        String townId = getNextTownID();
        TownData newTown = new TownData(townId, townName, tanPlayer);

        put(townId,newTown);
        return newTown;
    }

    private @NotNull String getNextTownID() {
        String townId = "T"+ nextID;
        nextID++;
        return townId;
    }

    @Override
    public void deleteTown(Town townData) {
        dataMap.remove(townData.getID());
    }
}
