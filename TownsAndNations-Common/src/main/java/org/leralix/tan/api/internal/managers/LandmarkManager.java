package org.leralix.tan.api.internal.managers;

import org.bukkit.Location;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.tan.api.getters.TanLandmarkManager;
import org.tan.api.interfaces.TanLandmark;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class LandmarkManager implements TanLandmarkManager {

    private final LandmarkStorage landmarkStorage;
    private static LandmarkManager instance;

    private LandmarkManager() {
        landmarkStorage = LandmarkStorage.getInstance();
    }

    public static LandmarkManager getInstance() {
        if (instance == null) {
            instance = new LandmarkManager();
        }
        return instance;
    }


    @Override
    public Collection<TanLandmark> getLandmarks() {
        return landmarkStorage.getAll().values().stream()
                .map(t -> (TanLandmark) t)
                .toList();
    }

    @Override
    public Optional<TanLandmark> getLandmark(String s) {
        return Optional.ofNullable(landmarkStorage.get(s));
    }

    @Override
    public TanLandmark getLandmark(UUID uuid) {
        return null;
    }

    @Override
    public TanLandmark createLandmark(double v, double v1, double v2, UUID uuid) {
        return null;
    }

    @Override
    public TanLandmark createLandmark(double v, double v1, double v2, String s) {
        return null;
    }

    @Override
    public TanLandmark createLandmark(Location location) {
        return null;
    }
}
