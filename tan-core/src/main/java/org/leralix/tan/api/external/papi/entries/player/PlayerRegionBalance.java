package org.leralix.tan.api.external.papi.entries.player;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.api.external.papi.entries.PapiEntry;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class PlayerRegionBalance extends PapiEntry {

  public PlayerRegionBalance() {
    super("player_region_balance");
  }

  @Override
  public String getData(OfflinePlayer player, @NotNull String params) {

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player.getUniqueId());

    if (tanPlayer == null) {
      return PLAYER_NOT_FOUND;
    }

    return tanPlayer.hasRegion()
        ? Double.toString(tanPlayer.getRegionSync().getBalance())
        : Lang.NO_REGION.get(tanPlayer);
  }
}
