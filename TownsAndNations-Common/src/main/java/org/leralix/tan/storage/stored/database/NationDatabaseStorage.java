package org.leralix.tan.storage.stored.database;

import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.territory.*;
import org.leralix.tan.storage.stored.NationStorage;
import org.leralix.tan.utils.constants.database.RedisConfig;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

import java.util.Map;

public class NationDatabaseStorage extends DatabaseStorage<NationDatabase, Nation> implements NationStorage {

    public NationDatabaseStorage(RedisConfig redisConfig) {
        super(new NationDbManager(redisConfig));
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
    public Nation newNation(String name, @NotNull Region capital) {
        NationData newNation = new NationData("N" + getNextID(), name, capital.getLeaderData(), capital);
        databaseManager.save(newNation);
        Nation loadedNation = getOrLoad(newNation.getID(), this::load);

        capital.setOverlord(loadedNation);
        return loadedNation;
    }

    @Override
    public Nation get(String nationID) {
        return getOrLoad(nationID, this::load);
    }


    @Override
    public boolean isNameUsed(String name) {
        return TerritoryUtil.isNameUsed(name, databaseManager.getAll());
    }

    @Override
    public void delete(String id) {
        databaseManager.delete(id);
    }

    @Override
    public Map<String, Nation> getAll() {
        return databaseManager.getMap();
    }

    @Override
    public void save() {
        // No need to implement this method as the database manager handles saving automatically
    }

    private NationDatabase load(String id) {
        Nation data = databaseManager.load(id);
        if (data == null) {
            return null;
        }
        return new NationDatabase(data, databaseManager);
    }
}
