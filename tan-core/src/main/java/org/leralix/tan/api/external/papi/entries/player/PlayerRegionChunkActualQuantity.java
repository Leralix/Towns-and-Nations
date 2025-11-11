package org.leralix.tan.api.external.papi.entries.player;

import org.leralix.tan.api.external.papi.entries.PapiEntry;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class PlayerRegionChunkActualQuantity extends PapiEntry {

  public PlayerRegionChunkActualQuantity() {
    super("player_region_chunk_actual_quantity");
  }

  @Override
  public String getData(OfflinePlayer player, @NotNull String params) {

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player.getUniqueId());

    if (tanPlayer == null) {
      return PLAYER_NOT_FOUND;
    }

    return tanPlayer.hasRegion()
        ? Integer.toString(tanPlayer.getRegionSync().getNumberOfClaimedChunk())
        : Lang.NO_REGION.get(tanPlayer);
  }
}
