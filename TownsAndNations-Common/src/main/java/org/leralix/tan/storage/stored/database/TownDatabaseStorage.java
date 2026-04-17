package org.leralix.tan.storage.stored.database;

import org.jetbrains.annotations.Nullable;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.data.territory.TownDataBase;
import org.leralix.tan.storage.stored.TownStorage;
import org.leralix.tan.utils.constants.database.RedisConfig;

import java.util.Map;

public class TownDatabaseStorage extends DatabaseStorage<TownDataBase, Town> implements TownStorage {

    public TownDatabaseStorage(RedisConfig redisConfig) {
        super(new TownDBManager(redisConfig));
    }

    private int getNextID() {
        int nextTownID = 0;
        for (Territory townData : getAll().values()) {
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
    public Town newTown(String townName, @Nullable ITanPlayer tanPlayer) {
        TownData newTown = new TownData("T" + getNextID(), townName, tanPlayer);
        databaseManager.save(newTown);
        return getOrLoad(newTown.getID(), this::load);
    }

    @Override
    public void deleteTown(Town townData) {
        databaseManager.delete(townData.getID());
    }

    @Override
    public Town get(String townId) {
        return getOrLoad(townId, this::load);
    }

    @Override
    public Map<String, Town> getAll() {
        return databaseManager.getMap();
    }

    @Override
    public void save() {
        // No need to implement this method as each town is saved immediately when created or modified.
    }

    private TownDataBase load(String id) {
        Town data = databaseManager.load(id);
        if(data == null){
            return null;
        }
        return new TownDataBase(data, databaseManager);
    }
}
