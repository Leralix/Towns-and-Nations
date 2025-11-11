package org.leralix.tan.api.internal.wrappers;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import org.leralix.lib.position.Vector2D;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.tan.api.interfaces.TanLandmark;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.TanProperty;
import org.tan.api.interfaces.TanTown;

public class TownDataWrapper extends TerritoryDataWrapper implements TanTown {

  private final TownData townData;

  private TownDataWrapper(TownData townData) {
    super(townData);
    this.townData = townData;
  }

  public static TownDataWrapper of(TownData townData) {
    if (townData == null) return null;
    return new TownDataWrapper(townData);
  }

  public int getLevel() {
    return townData.getNewLevel().getMainLevel();
  }

  public Collection<TanProperty> getProperties() {
    return townData.getPropertyDataMap().values().stream()
        .map(PropertyDataWrapper::of)
        .map(p -> (TanProperty) p)
        .toList();
  }

  public Collection<TanLandmark> getLandmarksOwned() {
    return LandmarkStorage.getInstance().getLandmarkOf(townData).stream()
        .map(LandmarkDataWrapper::of)
        .map(l -> (TanLandmark) l)
        .toList();
  }

  public Optional<Vector2D> getCapitalLocation() {
    return townData.getCapitalLocation();
  }

  public String getLeader() {
    return townData.getLeaderID();
  }

  @Override
  public Collection<TanPlayer> getMembers() {
    return townData.getITanPlayerList().stream()
        .map(TanPlayerWrapper::of)
        .collect(Collectors.toList());
  }

  public boolean isMember(Player player) {
    return townData.getPlayerIDList().contains(player.getUniqueId().toString());
  }

  public boolean isMember(UUID playerUUID) {
    return townData.getPlayerIDList().contains(playerUUID.toString());
  }
}
