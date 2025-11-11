package org.leralix.tan.api.external.papi.entries.player;

import org.leralix.tan.api.external.papi.entries.PapiEntry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class OtherPlayerRegionName extends PapiEntry {

  public OtherPlayerRegionName() {
    super("player_{}_region_name");
  }

  @Override
  public String getData(OfflinePlayer player, @NotNull String params) {

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player.getUniqueId());

    if (tanPlayer == null) {
      return PLAYER_NOT_FOUND;
    }

    String[] values = extractValues(params);
    OfflinePlayer playerSelected = Bukkit.getOfflinePlayer(values[0]);

    ITanPlayer otherTanPlayer = PlayerDataStorage.getInstance().getSync(playerSelected);

    return otherTanPlayer.hasRegion()
        ? otherTanPlayer.getRegionSync().getName()
        : Lang.NO_REGION.get(tanPlayer);
  }
}
