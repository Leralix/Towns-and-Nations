package org.leralix.tan.dataclass;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.utils.RandomUtil;

public class RareItem {
    private final int dropChance;
    private final ItemStack item;

    public RareItem(int dropChance, ItemStack rareMaterial) {
        this.dropChance = dropChance;
        this.item = rareMaterial;
    }

    public int getDropChance() {
        return dropChance;
    }

    public ItemStack getRareItem() {
        return item;
    }

    public Item spawn(World world, Location location) {
        int randInt = RandomUtil.getRandom().nextInt(1, 100);

        if(randInt <= dropChance){
            return world.dropItem(location, item);
        }
        return null;
    }
}