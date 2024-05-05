package org.tan.TownsAndNations.DataClass;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.tan.TownsAndNations.utils.ConfigUtil;
import org.tan.TownsAndNations.utils.EconomyUtil;

import java.util.Random;

public class RareItem {
    private final int dropChance;
    private final ItemStack item;
    private int price;

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
        Random rand = new Random();
        int int_random = rand.nextInt(1, 100);

        if(int_random <= dropChance){
            return world.dropItemNaturally(location, item);
        }
        return null;
    }
}