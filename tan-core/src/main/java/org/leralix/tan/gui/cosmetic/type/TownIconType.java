package org.leralix.tan.gui.cosmetic.type;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class TownIconType extends IconType {

  @Override
  protected ItemStack getItemStack(Player player) {
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    if (!tanPlayer.hasTown()) {
      return new ItemStack(Material.BARRIER);
    }
    return tanPlayer.getTownSync().getIcon();
  }
}
