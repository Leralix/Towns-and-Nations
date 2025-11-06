package org.leralix.tan.gui.cosmetic.type;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class PlayerLanguageIconType extends IconType {
  @Override
  protected ItemStack getItemStack(Player player) {
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    return tanPlayer.getLang().getIcon(tanPlayer.getLang());
  }
}
