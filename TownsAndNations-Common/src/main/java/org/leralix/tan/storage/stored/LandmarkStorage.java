package org.leralix.tan.storage.stored;

import org.bukkit.Location;
import org.leralix.tan.data.building.landmark.Landmark;
import org.leralix.tan.data.territory.Territory;

import java.util.List;
import java.util.Map;

public interface LandmarkStorage {

    Landmark addLandmark(Location position);

    Landmark get(String id);

    List<Landmark> getLandmarkOf(Territory territoryData);

    void generateAllResources();

    void delete(Landmark landmark);

    Map<String, Landmark> getAll();

    void save();

}
