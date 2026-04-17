package org.leralix.tan.storage.stored.database;

import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.*;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.RegionStorage;
import org.leralix.tan.utils.constants.database.RedisConfig;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

import java.util.Map;

public class RegionDatabaseStorage extends DatabaseStorage<RegionDatabase, Region> implements RegionStorage {

    public RegionDatabaseStorage(RedisConfig redisConfig) {
        super(new RegionDbManager(redisConfig));
    }

    private int getNextID() {
        int nextTownID = 0;
        for (Territory townData : databaseManager.getAll()) {
            String idString = townData.getID();
            if (idString != null && idString.length() >= 2) {
                String suffix = idString.substring(1);
                boolean isNumeric = suffix.chars().allMatch(Character::isDigit);
                if (isNumeric) {
                    int newID = Integer.parseInt(suffix);
                    if (newID >= nextTownID) {
                        nextTownID = newID + 1;
                    }
                }
            }
        }
        return nextTownID;
    }

    @Override
    public Region newRegion(String name, Town capital) {
        ITanPlayer newLeader = capital.getLeaderData();
        RegionData newRegion = new RegionData("R" + getNextID(), name, newLeader);
        databaseManager.save(newRegion);

        Region loadedRegion = getOrLoad(newRegion.getID(), this::load);

        capital.setOverlord(loadedRegion);
        FileUtil.addLineToHistory(Lang.REGION_CREATED_NEWSLETTER.get(newLeader.getNameStored(), name));
        return loadedRegion;
    }

    @Override
    public void deleteRegion(RegionData region) {
        databaseManager.delete(region.getID());
    }

    @Override
    public Region get(String regionID) {
        return getOrLoad(regionID, this::load);
    }

    @Override
    public boolean isNameUsed(String name) {
        return TerritoryUtil.isNameUsed(name, databaseManager.getAll());
    }

    @Override
    public Map<String, Region> getAll() {
        return databaseManager.getMap();
    }

    @Override
    public void save() {
        // No need to implement this method as the database manager handles saving automatically
    }

    private RegionDatabase load(String id) {
        Region data = databaseManager.load(id);
        if(data == null){
            return null;
        }
        return new RegionDatabase(data, databaseManager);
    }

}
