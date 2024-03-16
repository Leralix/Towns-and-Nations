package org.tan.TownsAndNations.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.tan.TownsAndNations.TownsAndNations;

public class CustomNBT {
    public static void addCustomStringTag(ItemStack item, String tagName, String tagValue) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(
                    new NamespacedKey(TownsAndNations.getPlugin(), tagName),
                    PersistentDataType.STRING,
                    tagValue
            );
            item.setItemMeta(meta);
        }
    }

    public static String getCustomStringTag(ItemStack item, String tagName) {
        if (item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(TownsAndNations.getPlugin(), tagName), PersistentDataType.STRING)) {
            return item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(TownsAndNations.getPlugin(), tagName), PersistentDataType.STRING);
        }
        return null;
    }


}
