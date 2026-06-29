package org.leralix.tan.storage.stored.database;

import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.*;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.NationStorage;
import org.leralix.tan.utils.constants.database.RedisConfig;
import org.leralix.tan.utils.file.FileUtil;

import java.util.HashMap;
import java.util.Map;

public class NationDatabaseStorage extends DatabaseStorage<NationDatabase, NationData> implements NationStorage {

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
        ITanPlayer newLeader = capital.getLeaderData();
        NationData newNation = new NationData("N" + getNextID(), name, newLeader, capital);
        databaseManager.save(newNation);

        Nation loadedNation = getOrLoad(newNation.getID(), this::load);

        capital.setOverlord(loadedNation);
        FileUtil.addLineToHistory(Lang.NATION_CREATED_NEWSLETTER.get(newLeader.getNameStored(), name));
        return loadedNation;
    }

    @Override
    public Nation get(String nationID) {
        return getOrLoad(nationID, this::load);
    }

    @Override
    public void delete(String id) {
        databaseManager.delete(id);
    }

    @Override
    public Map<String, Nation> getAll() {
        return new HashMap<>(databaseManager.getMap());
    }

    @Override
    public void save() {
        // No need to implement this method as the database manager handles saving automatically
    }

    private NationDatabase load(String id) {
        NationData data = databaseManager.load(id);
        if (data == null) {
            return null;
        }
        return new NationDatabase(data, databaseManager);
    }
}
