package org.leralix.tan.api.internal.wrappers;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.leralix.tan.dataclass.ITanPlayer;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.TanProperty;
import org.tan.api.interfaces.TanRegion;
import org.tan.api.interfaces.TanTown;

public class TanPlayerWrapper implements TanPlayer {

  private final ITanPlayer tanPlayer;

  private TanPlayerWrapper(ITanPlayer tanPlayer) {
    this.tanPlayer = tanPlayer;
  }

  public static TanPlayer of(ITanPlayer tanPlayer) {
    if (tanPlayer == null) {
      return null;
    }
    return new TanPlayerWrapper(tanPlayer);
  }

  @Override
  public UUID getUUID() {
    return tanPlayer.getUUID();
  }

  @Override
  public String getNameStored() {
    return tanPlayer.getNameStored();
  }

  @Override
  public void setNameStored(String name) {
    tanPlayer.setNameStored(name);
  }

  @Override
  public Optional<TanTown> getTown() {
    return Optional.ofNullable(TownDataWrapper.of(tanPlayer.getTownSync()));
  }

  @Override
  public boolean hasTown() {
    return tanPlayer.hasTown();
  }

  @Override
  public TanRegion getRegion() {
    return RegionDataWrapper.of(tanPlayer.getRegionSync());
  }

  @Override
  public boolean hasRegion() {
    return tanPlayer.hasRegion();
  }

  @Override
  public Collection<TanProperty> getPropertiesOwned() {
    return tanPlayer.getProperties().stream()
        .map(PropertyDataWrapper::of)
        .collect(Collectors.<TanProperty>toList());
  }

  @Override
  public Collection<TanProperty> getPropertiesForSale() {
    return tanPlayer.getProperties().stream()
        .filter(org.leralix.tan.dataclass.PropertyData::isForSale)
        .map(PropertyDataWrapper::of)
        .collect(Collectors.<TanProperty>toList());
  }

  @Override
  public Collection<TanProperty> getPropertiesRented() {
    return tanPlayer.getProperties().stream()
        .filter(org.leralix.tan.dataclass.PropertyData::isRented)
        .map(PropertyDataWrapper::of)
        .collect(Collectors.<TanProperty>toList());
  }
}
