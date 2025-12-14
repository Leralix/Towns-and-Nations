package org.leralix.tan.gui.cosmetic.type;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemIconBuilder extends IconType {

    protected final Material material;

    public ItemIconBuilder(Material material) {
        this.material = material;

    }

    @Override
    protected ItemStack getItemStack(Player player) {
        return new ItemStack(material);
    }

}
