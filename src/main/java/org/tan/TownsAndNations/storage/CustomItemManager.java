package org.tan.TownsAndNations.storage;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.tan.TownsAndNations.utils.config.ConfigTag;
import org.tan.TownsAndNations.utils.config.ConfigUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomItemManager {

    private static final HashMap<String, Integer> customItemsMap = new HashMap<>();

    public static void loadCustomItems() {
        List<Map<?, ?>> customItemsList = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getMapList("customRareItems");

        for (Map<?, ?> itemConfig : customItemsList) {
            String itemName = (String) itemConfig.get("item");
            Integer customTextureID = (Integer) itemConfig.getOrDefault("customTextureID", null);
            int value = (int) itemConfig.get("value");

            // Create the key for the HashMap
            String key;
            if (customTextureID != null) {
                key = itemName + ":" + customTextureID;
            } else {
                key = itemName;
            }

            customItemsMap.put(key, value);
        }
    }

    public static Integer getItemValue(ItemStack itemStack) {

        String itemName = itemStack.getType().name();
        ItemMeta itemMeta = itemStack.getItemMeta();

        String key;
        if (itemMeta.hasCustomModelData()) {
            key = itemName + ":" + itemMeta.getCustomModelData();
        } else {
            key = itemName;
        }

        return customItemsMap.get(key);
    }

    public static HashMap<String, Integer> getCustomItemsMap() {
        return customItemsMap;
    }
}
