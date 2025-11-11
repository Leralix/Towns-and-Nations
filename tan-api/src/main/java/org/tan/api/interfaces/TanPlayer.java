package org.tan.api.interfaces;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface TanPlayer {
  String getNameStored();

  void setNameStored(String name);

  UUID getUUID();

  boolean hasTown();

  Optional<TanTown> getTown();

  boolean hasRegion();

  TanRegion getRegion();

  Collection<TanProperty> getPropertiesOwned();

  Collection<TanProperty> getPropertiesRented();

  Collection<TanProperty> getPropertiesForSale();
}
