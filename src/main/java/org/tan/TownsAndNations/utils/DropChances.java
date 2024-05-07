package org.tan.TownsAndNations.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.tan.TownsAndNations.DataClass.RareItem;
import org.tan.TownsAndNations.Lang.Lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DropChances {

    private static final Map<String, RareItem> dropChances = new HashMap<>();

    public static void load(){


        FileConfiguration config = ConfigUtil.getCustomConfig("config.yml");

        if(!config.getBoolean("RARE_RESOURCES_SPAWN", true)){
            return;
        }
        loadDropChances("rareStone", config, getRareStone());
        loadDropChances("rareWood", config, getRareWood());
        loadDropChances("rareCrops", config, getRareCrops());
        loadDropChances("rareSoul", config, getRareSoul());
        loadDropChances("rareFish", config, getRareFish());
    }

    public static RareItem getRareItem(Block block) {
        return dropChances.get(block.getType().name());
    }
    public static RareItem getRareItem(Entity entity) {
        return getRareItem(entity.getType());
    }
    public static RareItem getRareItem(EntityType entityType) {
        return dropChances.get(entityType.name());
    }
    public static RareItem getRareItem(String name) {
        return dropChances.get(name);
    }
    public static Map<String, RareItem> getDropChances() {
        return dropChances;
    }

    private static void loadDropChances(String section, FileConfiguration config, ItemStack item) {
        for (String key : config.getConfigurationSection(section).getKeys(false)) {
            int dropChance = config.getInt(section + "." + key);
            dropChances.put(key, new RareItem(dropChance, item));
        }
    }

    public static ItemStack getRareStone(){
        ItemStack rareStoneItem = new ItemStack(Material.EMERALD);
        ItemMeta rareStoneItemMeta = rareStoneItem.getItemMeta();
        rareStoneItemMeta.setCustomModelData(101);
        rareStoneItemMeta.setDisplayName(Lang.ITEM_RARE_STONE.get());
        ArrayList<String> lore = new ArrayList<>();
        lore.add(Lang.RARE_ITEM_DESC_1.get());
        rareStoneItemMeta.setLore(lore);
        rareStoneItem.setItemMeta(rareStoneItemMeta);

        CustomNBT.addCustomStringTag(rareStoneItem, "tanRareItem", "rareStone");

        return rareStoneItem;
    }
    public static ItemStack getRareWood(){
        ItemStack rareWoodItem = new ItemStack(Material.STICK);
        ItemMeta rareStoneItemMeta = rareWoodItem.getItemMeta();
        rareStoneItemMeta.setCustomModelData(101);
        rareStoneItemMeta.setDisplayName(Lang.ITEM_RARE_WOOD.get());
        ArrayList<String> lore = new ArrayList<>();
        lore.add(Lang.RARE_ITEM_DESC_1.get());
        rareStoneItemMeta.setLore(lore);
        rareWoodItem.setItemMeta(rareStoneItemMeta);

        CustomNBT.addCustomStringTag(rareWoodItem, "tanRareItem", "rareWood");

        return rareWoodItem;
    }
    public static ItemStack getRareCrops(){
        ItemStack rareCropItem = new ItemStack(Material.WHEAT);
        ItemMeta rareStoneItemMeta = rareCropItem.getItemMeta();
        rareStoneItemMeta.setCustomModelData(101);
        rareStoneItemMeta.setDisplayName(Lang.ITEM_RARE_CROP.get());
        ArrayList<String> lore = new ArrayList<>();
        lore.add(Lang.RARE_ITEM_DESC_1.get());
        rareStoneItemMeta.setLore(lore);
        rareCropItem.setItemMeta(rareStoneItemMeta);

        CustomNBT.addCustomStringTag(rareCropItem, "tanRareItem", "rareCrops");

        return rareCropItem;
    }
    public static ItemStack getRareSoul(){
        ItemStack rareSoulItem = new ItemStack(Material.FLINT);
        ItemMeta rareStoneItemMeta = rareSoulItem.getItemMeta();
        rareStoneItemMeta.setCustomModelData(101);
        rareStoneItemMeta.setDisplayName(Lang.ITEM_RARE_SOUL.get());
        ArrayList<String> lore = new ArrayList<>();
        lore.add(Lang.RARE_ITEM_DESC_1.get());
        rareStoneItemMeta.setLore(lore);
        rareSoulItem.setItemMeta(rareStoneItemMeta);

        CustomNBT.addCustomStringTag(rareSoulItem, "tanRareItem", "rareSoul");

        return rareSoulItem;
    }

    public static ItemStack getRareFish() {
        ItemStack rareSoulItem = new ItemStack(Material.TROPICAL_FISH);
        ItemMeta rareStoneItemMeta = rareSoulItem.getItemMeta();
        rareStoneItemMeta.setCustomModelData(101);
        rareStoneItemMeta.setDisplayName(Lang.ITEM_RARE_FISH.get());
        ArrayList<String> lore = new ArrayList<>();
        lore.add(Lang.RARE_ITEM_DESC_1.get());
        rareStoneItemMeta.setLore(lore);
        rareSoulItem.setItemMeta(rareStoneItemMeta);

        CustomNBT.addCustomStringTag(rareSoulItem, "tanRareItem", "rareFish");

        return rareSoulItem;
    }

}
