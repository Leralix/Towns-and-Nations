package org.tan.towns_and_nations.DataClass;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RareItem {
    private final int dropChance;
    private final ItemStack rareMaterial;

    public RareItem(int dropChance, ItemStack rareMaterial) {
        this.dropChance = dropChance;
        this.rareMaterial = rareMaterial;
    }

    public int getDropChance() {
        return dropChance;
    }

    public ItemStack getRareItem() {
        return rareMaterial;
    }
}