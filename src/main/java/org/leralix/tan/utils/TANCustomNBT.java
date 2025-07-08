package org.leralix.tan.utils;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.TownDataStorage;


/**
 * This class is used to add custom NBT tags to items
 */
public class TANCustomNBT {

    private TANCustomNBT() {
        throw new IllegalStateException("Utility class");
    }
    /** Add a custom String tag to an item
     *
     * @param item      {@link ItemStack} to add the tag to.
     * @param tagName   Name of the tag.
     * @param tagValue  {@link String} value of the tag.
     */
    public static void addCustomStringTag(final @NotNull ItemStack item, final @NotNull  String tagName, final @NotNull String tagValue) {
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

    /**
     * Get a custom String tag from an item
     * @param item      The {@link ItemStack} to get the tag from.
     * @param tagName   The name of the tag.
     * @return          The value of the tag, or null if the tag does not exist.
     */
    @Nullable
    public static String getCustomStringTag(final @NotNull ItemStack item, final @NotNull String tagName) {
        if(item.getItemMeta() == null) return null;
        if (item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(TownsAndNations.getPlugin(), tagName), PersistentDataType.STRING)) {
            return item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(TownsAndNations.getPlugin(), tagName), PersistentDataType.STRING);
        }
        return null;
    }

    public static void setBockMetaData(final @NotNull Block block, final @NotNull String metaData, final @NotNull String value){
        block.setMetadata(metaData,
                new FixedMetadataValue(TownsAndNations.getPlugin(),value));
    }
    @Nullable
    public static String getBockMetaData(Block block, String metaData){
        if(!block.hasMetadata(metaData))
            return null;
        return block.getMetadata(metaData).get(0).asString();
    }

    public static void setBlocsData(){
        setSignData();
        setLandmarksData();
    }

    /**
     * Sets metadata for property sign blocks and the blocks directly beneath them for all properties in all towns.
     */
    public static void setSignData(){
        for( TownData townData : TownDataStorage.getInstance().getTownMap().values() ){
            for( PropertyData propertyData : townData.getPropertyDataMap().values() ){
                Block block = propertyData.getSign();
                Location blockBeneathLocation = propertyData.getSign().getLocation().add(0,-1,0);
                Block blockBeneath = blockBeneathLocation.getWorld().getBlockAt(blockBeneathLocation);

                setBockMetaData(block, "propertySign", propertyData.getTotalID());
                setBockMetaData(blockBeneath, "propertySign", propertyData.getTotalID());
            }
        }
    }

    public static void setLandmarksData(){
        for(Landmark landmark : LandmarkStorage.getInstance().getAll()){
            Block block = landmark.getChest();
            setBockMetaData(block, "LandmarkChest", landmark.getID());
        }
    }




}
