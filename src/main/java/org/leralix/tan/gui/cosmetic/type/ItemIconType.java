package org.leralix.tan.gui.cosmetic.type;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemIconType extends IconType {

    private final ItemStack itemStack;

    public ItemIconType(ItemStack icon) {
        super();
        this.itemStack = icon;
    }

    @Override
    protected ItemStack getItemStack(Player player) {
        return itemStack;
    }
}
