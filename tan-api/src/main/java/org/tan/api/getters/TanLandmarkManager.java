package org.tan.api.getters;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Location;
import org.tan.api.interfaces.TanLandmark;

public interface TanLandmarkManager {
  Collection<TanLandmark> getLandmarks();

  Optional<TanLandmark> getLandmark(String id);

  TanLandmark getLandmark(UUID uuid);

  TanLandmark createLandmark(double x, double y, double z, UUID worldUuid);

  TanLandmark createLandmark(double x, double y, double z, String worldName);

  TanLandmark createLandmark(Location location);
}
