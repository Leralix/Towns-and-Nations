package org.leralix.tan.gui.cosmetic.type;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomMaterialIcon extends ItemIconBuilder {

  private final int modelData;

  public CustomMaterialIcon(Material material, int modelData) {
    super(material);
    this.modelData = modelData;
  }

  @Override
  protected ItemStack getItemStack(Player player) {
    ItemStack itemStack = new ItemStack(material);

    ItemMeta itemMeta = itemStack.getItemMeta();
    if (itemMeta != null) {
      itemMeta.setCustomModelData(modelData);
      itemStack.setItemMeta(itemMeta);
    }
    return itemStack;
  }
}
