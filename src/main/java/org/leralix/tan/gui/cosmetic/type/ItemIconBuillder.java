package org.leralix.tan.gui.cosmetic.type;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemIconBuillder extends IconBuilder {

    private final Material material;

    public ItemIconBuillder(Material material) {
        this.material = material;

    }

    @Override
    protected ItemStack getItemStack(Player player) {
        return new ItemStack(material);
    }

}
