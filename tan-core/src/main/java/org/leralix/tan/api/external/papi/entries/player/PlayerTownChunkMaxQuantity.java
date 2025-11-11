package org.leralix.tan.api.external.papi.entries.player;

import org.leralix.tan.api.external.papi.entries.PapiEntry;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.upgrade.rewards.numeric.ChunkCap;

public class PlayerTownChunkMaxQuantity extends PapiEntry {

  public PlayerTownChunkMaxQuantity() {
    super("player_town_chunk_max_quantity");
  }

  @Override
  public String getData(OfflinePlayer player, @NotNull String params) {

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player.getUniqueId());

    if (tanPlayer == null) {
      return PLAYER_NOT_FOUND;
    }

    if (tanPlayer.hasTown()) {
      var town = tanPlayer.getTownSync();
      var level = town.getNewLevel().getStat(ChunkCap.class);

      if (level.isUnlimited()) {
        return "∞";
      } else {
        return Integer.toString(level.getMaxAmount());
      }
    }

    return Lang.NO_TOWN.get(tanPlayer);
  }
}
