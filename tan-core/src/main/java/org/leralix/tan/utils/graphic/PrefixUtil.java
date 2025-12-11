package org.leralix.tan.utils.graphic;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;

public class PrefixUtil {

  private PrefixUtil() {
    throw new AssertionError("Utility class");
  }

  public static void updatePrefix(Player player) {
    if (!Constants.enableTownTag()) {
      return;
    }
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player).join();

    if (tanPlayer.getTownSync() != null) {
      String prefix = tanPlayer.getTownSync().getColoredTag() + " ";

      player.playerListName(
          org.leralix.tan.utils.text.ComponentUtil.fromLegacy(prefix + player.getName()));
      player.displayName(
          org.leralix.tan.utils.text.ComponentUtil.fromLegacy(prefix + player.getName()));
    } else {
      player.playerListName(Component.text(player.getName()));
      player.displayName(Component.text(player.getName()));
    }
  }
}
