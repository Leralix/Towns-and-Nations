package org.tan.towns_and_nations.utils;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.tan.towns_and_nations.DataClass.RareItem;
import org.tan.towns_and_nations.Lang.Lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DropChances {

    private static final Map<Material, RareItem> dropChances = new HashMap<>();

    public static void load(){

        FileConfiguration config = ConfigUtil.getCustomConfig("config.yml");

        loadDropChances("rareStone", config, getRareStone());
        loadDropChances("rareWood", config, getRareWood());
        loadDropChances("rareCrops", config, getRareCrops());
    }


    public static RareItem getRareItem(Material material) {
        return dropChances.get(material);
    }

    private static void loadDropChances(String section, FileConfiguration config, ItemStack rareMaterial) {
        for (String key : config.getConfigurationSection(section).getKeys(false)) {
            int dropChance = config.getInt(section + "." + key);
            dropChances.put(Material.valueOf(key), new RareItem(dropChance, rareMaterial));
        }
    }

    private static ItemStack getRareStone(){
        ItemStack rareStoneItem = new ItemStack(Material.EMERALD);
        ItemMeta rareStoneItemMeta = rareStoneItem.getItemMeta();
        rareStoneItemMeta.setCustomModelData(101);
        rareStoneItemMeta.setDisplayName(Lang.ITEM_RARE_STONE.getTranslation());
        ArrayList<String> lore = new ArrayList<>();
        lore.add(Lang.RARE_ITEM_DESC_1.getTranslation());
        rareStoneItemMeta.setLore(lore);
        rareStoneItem.setItemMeta(rareStoneItemMeta);
        return rareStoneItem;
    }
    private static ItemStack getRareWood(){
        ItemStack rareStoneItem = new ItemStack(Material.STICK);
        ItemMeta rareStoneItemMeta = rareStoneItem.getItemMeta();
        rareStoneItemMeta.setCustomModelData(101);
        rareStoneItemMeta.setDisplayName(Lang.ITEM_RARE_WOOD.getTranslation());
        ArrayList<String> lore = new ArrayList<>();
        lore.add(Lang.RARE_ITEM_DESC_1.getTranslation());
        rareStoneItemMeta.setLore(lore);
        rareStoneItem.setItemMeta(rareStoneItemMeta);
        return rareStoneItem;
    }
    private static ItemStack getRareCrops(){
        ItemStack rareStoneItem = new ItemStack(Material.WHEAT);
        ItemMeta rareStoneItemMeta = rareStoneItem.getItemMeta();
        rareStoneItemMeta.setCustomModelData(101);
        rareStoneItemMeta.setDisplayName(Lang.ITEM_RARE_CROP.getTranslation());
        ArrayList<String> lore = new ArrayList<>();
        lore.add(Lang.RARE_ITEM_DESC_1.getTranslation());
        rareStoneItemMeta.setLore(lore);
        rareStoneItem.setItemMeta(rareStoneItemMeta);
        return rareStoneItem;
    }
}
