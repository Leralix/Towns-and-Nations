package org.leralix.tan.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.tan.dataclass.RareItem;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class manage drop chances of rare items
 */
public class DropChances {

    /**
     * Storing every different rareItem in a map
     */
    private static final Map<String, RareItem> dropChances = new HashMap<>();

    /**
     * Load the class and fill up the drop chances.
     */
    public static void load(){


        FileConfiguration config = ConfigUtil.getCustomConfig(ConfigTag.MAIN);

        if(!config.getBoolean("RARE_RESOURCES_SPAWN", true)){
            return;
        }
        loadDropChances(config,"rareStone",  getRareStone());
        loadDropChances(config,"rareWood", getRareWood());
        loadDropChances(config,"rareCrops", getRareCrops());
        loadDropChances(config,"rareSoul", getRareSoul());
        loadDropChances(config,"rareFish", getRareFish());
    }

    /**
     * Get the {@link RareItem} from a {@link Block}
     * @param block     The block
     * @return          The {@link RareItem} dropped from the block or null if the {@link Block} is not giving {@link RareItem}
     */
    public static RareItem getRareItem(Block block) {
        return dropChances.get(block.getType().name());
    }
    /**
     * Get the {@link RareItem} from a {@link Entity}
     * @param entity    The {@link Entity}
     * @return          The {@link RareItem} dropped from the block or null if the {@link Entity} not giving {@link RareItem}
     */
    public static RareItem getRareItem(Entity entity) {
        return getRareItem(entity.getType());
    }
    /**
     * Get the {@link RareItem} from a {@link EntityType}
     * @param entityType    The {@link EntityType}
     * @return              The {@link RareItem} dropped from the block or null if the {@link EntityType} is not giving {@link RareItem}
     */
    public static RareItem getRareItem(EntityType entityType) {
        return dropChances.get(entityType.name());
    }
    /**
     * Get the {@link RareItem} from a {@link EntityType}
     * @param name      The name of the rare item
     * @return          The {@link RareItem} dropped from the block or null if the name is not giving {@link RareItem}
     */
    public static RareItem getRareItem(String name) {
        return dropChances.get(name);
    }

    /**
     * get the map storing every rare items
     * @return the map storing all {@link RareItem}
     */
    public static Map<String, RareItem> getDropChances() {
        return dropChances;
    }

    /**
     * Load every drop chance for a single item.
     * @param config    The {@link FileConfiguration} where the section is.
     * @param section   The name of the section in the configuration file.
     * @param item      The custom {@link ItemStack} dropped by this entry.
     */
    private static void loadDropChances(FileConfiguration config, String section, ItemStack item) {
        ConfigurationSection confSec = config.getConfigurationSection(section);
        if(confSec == null)
            return;
        for (String key : confSec.getKeys(false)) {
            int dropChance = config.getInt(section + "." + key);
            dropChances.put(key, new RareItem(dropChance, item));
        }
    }

    private static ItemStack createRareItem(Material material, Lang name, String tagValue){
        ItemStack rareItem = new ItemStack(material);
        ItemMeta rareStoneItemMeta = rareItem.getItemMeta();
        if(rareStoneItemMeta != null){
            rareStoneItemMeta.setCustomModelData(101);
            rareStoneItemMeta.setDisplayName(name.get());
            ArrayList<String> lore = new ArrayList<>();
            lore.add(Lang.RARE_ITEM_DESC_1.get());
            rareStoneItemMeta.setLore(lore);
            rareItem.setItemMeta(rareStoneItemMeta);
        }
        CustomNBT.addCustomStringTag(rareItem, "tanRareItem", tagValue);
        return rareItem;
    }

    public static ItemStack getRareStone(){
        return createRareItem(Material.EMERALD, Lang.ITEM_RARE_STONE, "rareStone");
    }
    public static ItemStack getRareWood(){
        return createRareItem(Material.STICK, Lang.ITEM_RARE_WOOD, "rareWood");
    }
    public static ItemStack getRareCrops(){
        return createRareItem(Material.WHEAT, Lang.ITEM_RARE_CROP, "rareCrops");
    }
    public static ItemStack getRareSoul(){
        return createRareItem(Material.FLINT, Lang.ITEM_RARE_SOUL, "rareSoul");
    }
    public static ItemStack getRareFish() {
        return createRareItem(Material.TROPICAL_FISH, Lang.ITEM_RARE_FISH, "rareFish");
    }

}
