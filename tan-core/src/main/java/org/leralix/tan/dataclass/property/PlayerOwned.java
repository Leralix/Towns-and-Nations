package org.leralix.tan.dataclass.property;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class PlayerOwned extends AbstractOwner {

  private final String playerID;

  public PlayerOwned(ITanPlayer player) {
    this(player.getUUID().toString());
  }

  public PlayerOwned(String playerID) {
    super(OwnerType.PLAYER);
    this.playerID = playerID;
  }

  public String getPlayerID() {
    return playerID;
  }

  @Override
  public String getName() {
    return Bukkit.getOfflinePlayer(UUID.fromString(playerID)).getName();
  }

  @Override
  public boolean canAccess(ITanPlayer tanPlayer) {
    return tanPlayer.getID().equals(playerID);
  }

  @Override
  public void addToBalance(double amount) {
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(playerID);
    EconomyUtil.addFromBalance(tanPlayer, amount);
  }
}
