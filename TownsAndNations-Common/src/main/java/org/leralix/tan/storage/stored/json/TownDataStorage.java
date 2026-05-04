package org.leralix.tan.storage.stored.json;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.Nullable;
import org.leralix.tan.data.building.property.owner.AbstractOwner;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.data.territory.cosmetic.ICustomIcon;
import org.leralix.tan.data.territory.permission.ChunkPermissionType;
import org.leralix.tan.data.territory.permission.RelationPermission;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.storage.stored.TerritoryStorage;
import org.leralix.tan.storage.stored.TownStorage;
import org.leralix.tan.storage.typeadapter.EnumMapDeserializer;
import org.leralix.tan.storage.typeadapter.EnumMapKeyValueDeserializer;
import org.leralix.tan.storage.typeadapter.IconAdapter;
import org.leralix.tan.storage.typeadapter.OwnerDeserializer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TownDataStorage extends TerritoryStorage<Town> implements TownStorage {

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
    }

    @Override
    public Town newTown(String townName, @Nullable ITanPlayer tanPlayer){
        String townId = generateNextID("T");
        TownData newTown = new TownData(townId, townName, tanPlayer);

        put(townId,newTown);
        return newTown;
    }

    @Override
    public void deleteTown(Town townData) {
        dataMap.remove(townData.getID());
    }
}
