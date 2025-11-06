package org.leralix.tan.gui.cosmetic.type;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NoIconType extends IconType {
  @Override
  protected ItemStack getItemStack(Player player) {
    return new ItemStack(Material.AIR);
  }
}
