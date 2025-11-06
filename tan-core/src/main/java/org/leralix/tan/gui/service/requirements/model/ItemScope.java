package org.leralix.tan.gui.service.requirements.model;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.lang.LangType;

public abstract class ItemScope {

  public boolean isInScope(ItemStack itemStack) {
    return isInScope(itemStack.getType());
  }

  public abstract boolean isInScope(Material material);

  public abstract String getName(LangType langType);
}
