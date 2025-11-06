package org.leralix.tan.api.internal.wrappers;

import java.util.Optional;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.tan.api.interfaces.TanPlayer;
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
  public String getName() {
    return tanPlayer.getNameStored();
  }

  @Override
  public Optional<TanTown> getTown() {
    return Optional.ofNullable(TownDataWrapper.of(tanPlayer.getTownSync()));
  }

  @Override
  public Player getPlayer() {
    return tanPlayer.getPlayer();
  }
}
