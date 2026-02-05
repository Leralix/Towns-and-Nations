package org.leralix.tan.utils.deprecated;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * The class used to manage every head related commands
 */
public class HeadUtils {
    private HeadUtils() {
        throw new IllegalStateException("Utility class");
    }


    /**
     * Create an {@link ItemStack} with custom Lore
     *
     * @param itemMaterial The data of the region to display
     * @param itemName     The display name of the item
     * @param loreLines    The lore of the item
     * @return The ItemStack displaying the town
     */
    public static ItemStack createCustomItemStack(Material itemMaterial, String itemName, String... loreLines) {
        List<String> lore = List.of(loreLines);
        return createCustomItemStack(itemMaterial, itemName, lore);
    }

    /**
     * Create an {@link ItemStack} with custom Lore.
     *
     * @param itemMaterial The data of the region to display.
     * @param itemName     The display name of the item.
     * @param lore         The lore of the item.
     * @return The ItemStack displaying the town.
     */
    public static ItemStack createCustomItemStack(Material itemMaterial, String itemName, List<String> lore) {
        ItemStack item = new ItemStack(itemMaterial);
        return createCustomItemStack(item, itemName, lore);
    }

    public static ItemStack createCustomItemStack(ItemStack item, String itemName, List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + itemName);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
