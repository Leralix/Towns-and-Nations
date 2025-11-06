package org.leralix.tan.gui.cosmetic.type;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class IconType {

  protected abstract ItemStack getItemStack(Player player);
}
