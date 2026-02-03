package org.leralix.tan.utils.gameplay;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.building.fort.Fort;
import org.leralix.tan.data.building.landmark.Landmark;
import org.leralix.tan.data.building.property.PropertyData;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.storage.stored.FortStorage;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;


/**
 * This class is used to add custom NBT tags to items
 */
public class TANCustomNBT {

    private static final String PROPERTY_SIGN_METADATA = "propertySign";

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

    public static void removeBockMetaData(final @NotNull Block block, final @NotNull String metaData){
        block.removeMetadata(metaData, TownsAndNations.getPlugin());
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
        setFortData();
    }

    private static void setFortData() {
        for(Fort fort : FortStorage.getInstance().getForts()){
            Vector3D flagPosition = fort.getPosition();

            if(flagPosition.getWorld() == null){
                FortStorage.getInstance().delete(fort);
                continue;
            }

            setProtectedBlockData(fort);
        }
    }

    public static void setProtectedBlockData(Fort fort) {
        Vector3D flagPosition = fort.getPosition();

        Block baseBlock = flagPosition.getLocation().getBlock();
        Block flagBlock = flagPosition.getLocation().add(0, 1, 0).getBlock();

        setBockMetaData(baseBlock, "fortFlag", fort.getID());
        setBockMetaData(flagBlock, "fortFlag", fort.getID());
    }


    /**
     * Sets metadata for property sign blocks and the blocks directly beneath them for all properties in all towns.
     */
    public static void setSignData(){
        for( TownData townData : TownDataStorage.getInstance().getAll().values() ){
            Iterator<PropertyData> iterator = townData.getPropertyDataMap().values().iterator();
            while (iterator.hasNext()) {
                PropertyData propertyData = iterator.next();

                Optional<Block> optBlock = propertyData.getSign();
                if (optBlock.isPresent()) {
                    Block block = optBlock.get();
                    Location blockBeneathLocation = block.getLocation().add(0,-1,0);
                    Block blockBeneath = blockBeneathLocation.getWorld().getBlockAt(blockBeneathLocation);

                    setBockMetaData(block, PROPERTY_SIGN_METADATA, propertyData.getTotalID());
                    setBockMetaData(blockBeneath, PROPERTY_SIGN_METADATA, propertyData.getTotalID());
                } else {
                    iterator.remove();
                }
            }
        }
    }

    public static void setLandmarksData(){
        Iterator<Landmark> landmarkIterator = LandmarkStorage.getInstance().getAll().values().iterator();
        List<Landmark> landmarksToDelete = new ArrayList<>();
        while (landmarkIterator.hasNext()){
            Landmark landmark = landmarkIterator.next();
            var optChest = landmark.getChest();
            if(optChest.isPresent()){
                setBockMetaData(optChest.get(), "LandmarkChest", landmark.getID());
            }
            else {
                landmarksToDelete.add(landmark);
            }
        }
        for (Landmark landmark : landmarksToDelete){
            LandmarkStorage.getInstance().delete(landmark);
        }
    }



}
