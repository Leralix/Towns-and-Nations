package org.leralix.tan.storage.stored.database;

import org.bukkit.Location;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.building.landmark.Landmark;
import org.leralix.tan.data.building.landmark.LandmarkData;
import org.leralix.tan.data.building.landmark.LandmarkDatabase;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.utils.constants.database.RedisConfig;

import java.util.List;
import java.util.Map;

public class LandmarkDatabaseStorage extends DatabaseStorage<LandmarkDatabase, Landmark> implements LandmarkStorage {

    public LandmarkDatabaseStorage(RedisConfig redisConfig) {
        super(new LandmarkDbManager(redisConfig));
    }

    private int getNextID() {
        int nextTownID = 0;
        for (Landmark landmark : getAll().values()) {
            String idString = landmark.getID();
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
    public Landmark addLandmark(Location position) {
        Vector3D vector3D = new Vector3D(position);
        String landmarkID = "L" + getNextID();
        LandmarkData landmark = new LandmarkData(landmarkID, vector3D);
        databaseManager.save(landmark);
        TownsAndNations.getPlugin().getClaimStorage().claimLandmarkChunk(position.getChunk(), landmarkID);
        save();
        return landmark;
    }

    @Override
    public Landmark get(String id) {
        return getOrLoad(id, this::load);
    }

    @Override
    public List<Landmark> getLandmarkOf(Territory territoryData) {
        return getAll().values().stream()
                .filter(landmark -> landmark.isOwnedBy(territoryData))
                .toList();
    }

    @Override
    public void generateAllResources() {
        for (Landmark landmark : getAll().values()) {
            landmark.generateResources();
        }
    }

    @Override
    public void delete(Landmark landmark) {
        databaseManager.delete(landmark.getID());
    }

    @Override
    public Map<String, Landmark> getAll() {
        return databaseManager.getMap();
    }

    @Override
    public void save() {
        // No need for DB storage.
    }

    private LandmarkDatabase load(String id) {
        Landmark data = databaseManager.load(id);
        if (data == null) {
            return null;
        }
        return new LandmarkDatabase(data, databaseManager);
    }

}
