package org.tan.api.interfaces;

import java.util.Collection;
import java.util.Optional;
import org.leralix.lib.position.Vector2D;

public interface TanTown extends TanTerritory {
  int getLevel();

  Collection<TanProperty> getProperties();

  Collection<TanLandmark> getLandmarksOwned();

  Optional<Vector2D> getCapitalLocation();
}
