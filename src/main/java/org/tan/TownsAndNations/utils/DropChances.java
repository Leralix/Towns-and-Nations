package org.tan.TownsAndNations.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.tan.TownsAndNations.DataClass.RareItem;
import org.tan.TownsAndNations.Lang.Lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DropChances {

    private static final Map<String, RareItem> dropChances = new HashMap<>();

    public static void load(){

        FileConfiguration config = ConfigUtil.getCustomConfig("config.yml");

        loadDropChances("rareStone", config, getRareStone());
        loadDropChances("rareWood", config, getRareWood());
        loadDropChances("rareCrops", config, getRareCrops());
    }


    public static RareItem getRareItem(Block block) {
        System.out.println(block.getType().name());
        return dropChances.get(block.getType().name());
    }

    public static Map<String, RareItem> getDropChances(){
        return dropChances;
    }

    private static void loadDropChances(String section, FileConfiguration config, ItemStack rareMaterial) {
        for (String key : config.getConfigurationSection(section).getKeys(false)) {
            int dropChance = config.getInt(section + "." + key);

            dropChances.put(key, new RareItem(dropChance, rareMaterial));
        }
    }

    public static ItemStack getRareStone(){
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
    public static ItemStack getRareWood(){
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
    public static ItemStack getRareCrops(){
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
