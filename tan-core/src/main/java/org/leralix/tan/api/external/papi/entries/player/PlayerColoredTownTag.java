package org.leralix.tan.api.external.papi.entries.player;

import org.leralix.tan.api.external.papi.entries.PapiEntry;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class PlayerColoredTownTag extends PapiEntry {

  public PlayerColoredTownTag() {
    super("player_colored_town_tag");
  }

  @Override
  public String getData(OfflinePlayer player, @NotNull String params) {

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player.getUniqueId());

    if (tanPlayer == null) {
      return PLAYER_NOT_FOUND;
    }

    return tanPlayer.hasTown()
        ? tanPlayer.getTownSync().getColoredTag()
        : Lang.NO_TOWN.get(tanPlayer);
  }
}
