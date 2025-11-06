package org.leralix.tan.utils.graphic;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;

/** Utility class for handling prefix */
public class PrefixUtil {

  private PrefixUtil() {
    throw new AssertionError("Utility class");
  }

  /**
   * Add the town prefix to a player's name
   *
   * @param player The player to add the prefix to
   */
  public static void updatePrefix(Player player) {
    if (!Constants.enableTownTag()) {
      return;
    }
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);

    if (tanPlayer.getTownSync() != null) {
      String prefix = tanPlayer.getTownSync().getColoredTag() + " ";

      player.setPlayerListName(prefix + player.getName());
      player.setDisplayName(prefix + player.getName());
    } else {
      player.setPlayerListName(player.getName());
      player.setDisplayName(player.getName());
    }
  }
}
