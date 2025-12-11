package org.leralix.tan.utils.gameplay;

import java.util.Iterator;
import java.util.Optional;
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
import org.leralix.tan.storage.impl.FortDataStorage;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.wars.fort.Fort;

public class TANCustomNBT {

  private TANCustomNBT() {
    throw new IllegalStateException("Utility class");
  }

  public static void addCustomStringTag(
      final @NotNull ItemStack item,
      final @NotNull String tagName,
      final @NotNull String tagValue) {
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.getPersistentDataContainer()
          .set(
              new NamespacedKey(TownsAndNations.getPlugin(), tagName),
              PersistentDataType.STRING,
              tagValue);
      item.setItemMeta(meta);
    }
  }

  @Nullable public static String getCustomStringTag(
      final @NotNull ItemStack item, final @NotNull String tagName) {
    if (item.getItemMeta() == null) return null;
    if (item.getItemMeta()
        .getPersistentDataContainer()
        .has(new NamespacedKey(TownsAndNations.getPlugin(), tagName), PersistentDataType.STRING)) {
      return item.getItemMeta()
          .getPersistentDataContainer()
          .get(new NamespacedKey(TownsAndNations.getPlugin(), tagName), PersistentDataType.STRING);
    }
    return null;
  }

  public static void setBockMetaData(
      final @NotNull Block block, final @NotNull String metaData, final @NotNull String value) {
    block.setMetadata(metaData, new FixedMetadataValue(TownsAndNations.getPlugin(), value));
  }

  public static void removeBockMetaData(
      final @NotNull Block block, final @NotNull String metaData) {
    block.removeMetadata(metaData, TownsAndNations.getPlugin());
  }

  @Nullable public static String getBockMetaData(Block block, String metaData) {
    if (!block.hasMetadata(metaData)) return null;
    return block.getMetadata(metaData).get(0).asString();
  }

  public static void setBlocsData() {
    setSignData();
    setLandmarksData();
    setFortData();
  }

  private static void setFortData() {
    for (Fort fort : FortDataStorage.getInstance().getForts()) {
      fort.setProtectedBlockData();
    }
  }

  public static void setSignData() {
    for (TownData townData : TownDataStorage.getInstance().getAllSync().values()) {
      Iterator<PropertyData> iterator = townData.getPropertyDataMap().values().iterator();
      while (iterator.hasNext()) {
        PropertyData propertyData = iterator.next();

        Optional<Block> optBlock = propertyData.getSign();
        if (optBlock.isPresent()) {
          Block block = optBlock.get();
          Location blockBeneathLocation = block.getLocation().add(0, -1, 0);
          Block blockBeneath = blockBeneathLocation.getWorld().getBlockAt(blockBeneathLocation);

          setBockMetaData(block, "propertySign", propertyData.getTotalID());
          setBockMetaData(blockBeneath, "propertySign", propertyData.getTotalID());
        } else {
          iterator.remove();
        }
      }
    }
  }

  public static void setLandmarksData() {
    for (Landmark landmark : LandmarkStorage.getInstance().getAllSync().values()) {
      landmark
          .getChest()
          .ifPresent(block -> setBockMetaData(block, "LandmarkChest", landmark.getID()));
    }
  }
}
