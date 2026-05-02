package org.leralix.tan.storage.impl;

import org.jetbrains.annotations.NotNull;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.data.building.fort.Fort;
import org.leralix.tan.data.building.fort.FortData;
import org.leralix.tan.data.building.fort.FortDatabase;
import org.leralix.tan.data.building.fort.FortDbManager;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.FortStorage;
import org.leralix.tan.storage.stored.database.DatabaseStorage;
import org.leralix.tan.utils.constants.database.RedisConfig;

import java.util.ArrayList;
import java.util.List;

public class FortDatabaseStorage extends DatabaseStorage<FortDatabase, FortData> implements FortStorage {

    public FortDatabaseStorage(RedisConfig redisConfig) {
        super(new FortDbManager(redisConfig));
    }

    private int getNextFortID() {
        int nextFortID = 0;
        for(Fort fort : getForts()) {
            try {
                int idNum = Integer.parseInt(fort.getID().substring(1));
                if (idNum >= nextFortID) {
                    nextFortID = idNum + 1;
                }
            } catch (NumberFormatException e) {
                // Skip non-numeric IDs
            }
        }
        return nextFortID;
    }

    @Override
    public List<Fort> getForts() {
        return new ArrayList<>(databaseManager.getAll());
    }

    @Override
    public Fort getFort(String fortID) {
        return getOrLoad(fortID, this::load);
    }

    @Override
    public Fort register(Vector3D position, Territory owningTerritory) {
        int newFortID = getNextFortID();
        String id = "F" + newFortID;
        FortData fort = new FortData(id, position, Lang.DEFAULT_FORT_NAME.get(Lang.getServerLang(), Integer.toString(newFortID)), owningTerritory);
        databaseManager.save(fort);
        return getOrLoad(id, this::load);
    }

    @Override
    public void delete(String fortID) {
        databaseManager.delete(fortID);
    }

    @Override
    public void save() {
        // No automatic save for DB mode
    }

    private @NotNull FortDatabase load(String id) {
        FortData data = databaseManager.load(id);
        return new FortDatabase(data, databaseManager);
    }
}
