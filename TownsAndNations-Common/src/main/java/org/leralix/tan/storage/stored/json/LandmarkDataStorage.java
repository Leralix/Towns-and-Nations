package org.leralix.tan.storage.stored.json;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.bukkit.Location;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.building.landmark.Landmark;
import org.leralix.tan.data.building.landmark.LandmarkData;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.storage.stored.LandmarkStorage;

import java.util.HashMap;
import java.util.List;

public class LandmarkDataStorage extends JsonStorage<Landmark> implements LandmarkStorage {

    private int newLandmarkID;

    public LandmarkDataStorage() {
        super("TAN - Landmarks.json",
                new TypeToken<HashMap<String, LandmarkData>>() {
                }.getType(),
                new GsonBuilder()
                        .setPrettyPrinting()
                        .create()
        );
        newLandmarkID = getNewLandmarkID();
    }

    @Override
    public Landmark addLandmark(Location position) {
        Vector3D vector3D = new Vector3D(position);
        String landmarkID = "L" + newLandmarkID;
        LandmarkData landmark = new LandmarkData(landmarkID, vector3D);
        put(landmarkID, landmark);
        newLandmarkID++;
        TownsAndNations.getPlugin().getClaimStorage().claimLandmarkChunk(position.getChunk(), landmarkID);
        return landmark;
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
        delete(landmark.getID());
    }

    private int getNewLandmarkID() {
        int id = 0;
        for (String ids : getAll().keySet()) {
            int newID = Integer.parseInt(ids.substring(1));
            if (newID > id)
                id = newID;
        }
        return id + 1;
    }

}
