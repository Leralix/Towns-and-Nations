package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.RegionData;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.data.territory.cosmetic.ICustomIcon;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.typeadapter.EnumMapDeserializer;
import org.leralix.tan.storage.typeadapter.IconAdapter;
import org.leralix.tan.utils.file.FileUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RegionDataStorage extends JsonStorage<RegionData> {

    private int nextID;
    private static RegionDataStorage instance;

    public static RegionDataStorage getInstance() {
        if(instance == null)
            instance = new RegionDataStorage();
        return instance;
    }

    private RegionDataStorage() {
        super("TAN - Regions.json",
                new TypeToken<LinkedHashMap<String, RegionData>>() {}.getType(),
                new GsonBuilder()
                        .registerTypeAdapter(new TypeToken<Map<TownRelation, List<String>>>() {}.getType(),new EnumMapDeserializer<>(TownRelation.class, new TypeToken<List<String>>(){}.getType()))
                        .registerTypeAdapter(ICustomIcon.class, new IconAdapter())
                        .setPrettyPrinting()
                        .create());
    }

    public RegionData createNewRegion(String name, TownData capital){

        ITanPlayer newLeader = capital.getLeaderData();

        String regionID = generateNextID();

        RegionData newRegion = new RegionData(regionID, name, newLeader);
        put(regionID, newRegion);
        capital.setOverlord(newRegion);

        FileUtil.addLineToHistory(Lang.REGION_CREATED_NEWSLETTER.get(newLeader.getNameStored(), name));
        return newRegion;
    }

    private @NotNull String generateNextID() {
        String regionID = "R"+nextID;
        nextID++;
        return regionID;
    }

    public RegionData get(ITanPlayer tanPlayer){
        TownData town = TownDataStorage.getInstance().get(tanPlayer);
        if(town == null)
            return null;
        return town.getRegion().orElse(null);
    }

    public void deleteRegion(RegionData region){
        delete(region.getID());
    }

    public boolean isNameUsed(String name){
        for (RegionData region : getAll().values()){
            if(region.getName().equalsIgnoreCase(name))
                return true;
        }
        return false;
    }

    @Override
    public void load() {
        super.load();
        int id = 0;
        for (String keys : getAll().keySet()) {
            int newID =  Integer.parseInt(keys.substring(1));
            if(newID > id)
                id = newID;
        }
        nextID = id+1;
    }

    @Override
    public void reset() {
        instance = null;
    }
}
