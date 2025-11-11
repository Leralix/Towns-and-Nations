package org.leralix.tan.api.external.papi.entries.player;

import org.leralix.tan.api.external.papi.entries.PapiEntry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class OtherPlayerTownName extends PapiEntry {

  public OtherPlayerTownName() {
    super("player_{}_town_name");
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

    return otherTanPlayer.hasTown()
        ? otherTanPlayer.getTownSync().getName()
        : Lang.NO_TOWN.get(tanPlayer);
  }
}
