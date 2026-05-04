package org.leralix.tan.storage.stored.json;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Region;
import org.leralix.tan.data.territory.RegionData;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.data.territory.cosmetic.ICustomIcon;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.RegionStorage;
import org.leralix.tan.storage.stored.TerritoryStorage;
import org.leralix.tan.storage.typeadapter.EnumMapDeserializer;
import org.leralix.tan.storage.typeadapter.IconAdapter;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RegionDataStorage extends TerritoryStorage<Region> implements RegionStorage {


    public RegionDataStorage() {
        super("TAN - Regions.json",
                new TypeToken<LinkedHashMap<String, RegionData>>() {}.getType(),
                new GsonBuilder()
                        .registerTypeAdapter(new TypeToken<Map<TownRelation, List<String>>>() {}.getType(),new EnumMapDeserializer<>(TownRelation.class, new TypeToken<List<String>>(){}.getType()))
                        .registerTypeAdapter(ICustomIcon.class, new IconAdapter())
                        .setPrettyPrinting()
                        .create());
    }

    @Override
    public Region newRegion(String name, Town capital){
        ITanPlayer newLeader = capital.getLeaderData();
        String regionID = generateNextID("R");
        RegionData newRegion = new RegionData(regionID, name, newLeader);
        put(regionID, newRegion);

        capital.setOverlord(newRegion);
        FileUtil.addLineToHistory(Lang.REGION_CREATED_NEWSLETTER.get(newLeader.getNameStored(), name));
        return newRegion;
    }

    @Override
    public void deleteRegion(RegionData region){
        delete(region.getID());
    }

    @Override
    public boolean isNameUsed(String name){
        return TerritoryUtil.isNameUsed(name, dataMap.values());
    }

    @Override
    public void save() {

    }

}
