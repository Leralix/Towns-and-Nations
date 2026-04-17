package org.leralix.tan.utils.gameplay;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.building.fort.Fort;
import org.leralix.tan.data.building.landmark.Landmark;
import org.leralix.tan.data.building.property.PropertyData;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.storage.stored.FortStorage;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.TownStorage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;


/**
 * This class is used to add custom NBT tags to items
 */
public class TANCustomNBT {

    private static final String PROPERTY_SIGN_METADATA = "propertySign";

    private TANCustomNBT() {
        throw new IllegalStateException("Utility class");
    }

    public static void setBockMetaData(final @NotNull Block block, final @NotNull String metaData, final @NotNull String value){
        block.setMetadata(metaData,
                new FixedMetadataValue(TownsAndNations.getPlugin(),value));
    }

    public static void removeBockMetaData(final @NotNull Block block, final @NotNull String metaData){
        block.removeMetadata(metaData, TownsAndNations.getPlugin());
    }

    public static void setBlocsData(TownStorage townStorage, FortStorage fortStorage){
        setSignData(townStorage);
        setLandmarksData();
        setFortData(fortStorage);
    }

    private static void setFortData(FortStorage fortStorage) {
        for(Fort fort : fortStorage.getForts()){
            Vector3D flagPosition = fort.getPosition();

            // if the flag is in a deleted world, delete the fort
            if(flagPosition.getWorld() == null){
                fortStorage.delete(fort);
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
    public static void setSignData(TownStorage townStorage){
        for(Town townData : townStorage.getAll().values() ){
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
        LandmarkStorage landmarkStorage = TownsAndNations.getPlugin().getLandmarkStorage();
        for(Landmark landmark : new ArrayList<>(landmarkStorage.getAll().values())){
            var optChest = landmark.getChest();
            if(optChest.isPresent()){
                setBockMetaData(optChest.get(), "LandmarkChest", landmark.getID());
            }
            else {
                landmarkStorage.delete(landmark);
            }
        }
    }
}
