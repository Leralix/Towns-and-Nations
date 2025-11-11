package org.leralix.tan.api.external.papi.entries.player;

import org.leralix.tan.api.external.papi.entries.PapiEntry;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.storage.LocalChatStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class PlayerChatMode extends PapiEntry {

  public PlayerChatMode() {
    super("chat_mode");
  }

  @Override
  public String getData(OfflinePlayer player, @NotNull String params) {

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player.getUniqueId());

    if (tanPlayer == null) {
      return PLAYER_NOT_FOUND;
    }

    return LocalChatStorage.getPlayerChatScope(player.getUniqueId().toString())
        .getName(tanPlayer.getLang());
  }
}
