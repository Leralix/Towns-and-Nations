package org.leralix.tan.dataclass;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.WildernessChunk;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.LandmarkClaimedInternalEvent;
import org.leralix.tan.events.events.LandmarkUnclaimedInternalEvent;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.upgrade.TerritoryStats;
import org.leralix.tan.upgrade.rewards.percentage.LandmarkBonus;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.gameplay.TANCustomNBT;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

public class Landmark {

  private final String ID;
  private String name;
  private final Vector3D position;
  private String materialName;
  private int amount;
  private String ownerID;
  private int storedDays;
  private int storedLimit;

  public Landmark(String id, Vector3D position) {
    this.ID = id;
    this.name = Lang.SPECIFIC_LANDMARK_ICON_DEFAULT_NAME.get(Lang.getServerLang(), getID());
    this.position = position;
    this.materialName = "DIAMOND";
    this.amount = 2;
    this.storedDays = 0;
    this.storedLimit = Constants.getLandmarkStorageCapacity();
    spawnChest();
  }

  public String getID() {
    return this.ID;
  }

  public String getName() {
    return name;
  }

  public void setName(String newName) {
    this.name = newName;
  }

  public void setOwner(TownData newOwner) {
    EventManager.getInstance().callEvent(new LandmarkClaimedInternalEvent(this, newOwner));
    this.ownerID = newOwner.getID();
  }

  public void removeOwnership() {
    EventManager.getInstance().callEvent(new LandmarkUnclaimedInternalEvent(this, getOwner()));
    this.ownerID = null;
  }

  public String getOwnerID() {
    return ownerID;
  }

  public Vector3D getPosition() {
    return position;
  }

  public void spawnChest() {
    Block newBlock = position.getWorld().getBlockAt(position.getLocation());
    newBlock.setType(Material.CHEST);
    TANCustomNBT.setBockMetaData(newBlock, "LandmarkChest", getID());
  }

  public void dispawnChest() {
    Block newBlock = position.getWorld().getBlockAt(position.getLocation());

    TANCustomNBT.removeBockMetaData(newBlock, "LandmarkChest");
    newBlock.setType(Material.AIR);
  }

  public Optional<Block> getChest() {
    World world = position.getWorld();
    if (world == null) {
      return Optional.empty();
    }
    return Optional.of(position.getWorld().getBlockAt(position.getLocation()));
  }

  public Material getRessourceMaterial() {
    return Material.valueOf(materialName);
  }

  public ItemStack getResources() {
    ItemStack ressourcesItemStack = new ItemStack(getRessourceMaterial());
    ressourcesItemStack.setAmount(amount);
    return ressourcesItemStack;
  }

  public void generateResources() {
    if (!isOwned()) return;
    if (storedDays >= storedLimit) return;
    storedDays++;
  }

  public void setStoredLimit(int limit) {
    storedLimit = limit;
  }

  public boolean isOwned() {
    if (ownerID == null) return false;
    if (TerritoryUtil.getTerritory(ownerID) == null) {
      removeOwnership();
      return false;
    }
    return true;
  }

  private TownData getOwner() {
    return TownDataStorage.getInstance().getSync(ownerID);
  }

  public ItemStack getIcon(LangType langType) {
    Material rewardMaterial = Material.valueOf(materialName);
    ItemStack icon = new ItemStack(rewardMaterial, amount);
    ItemMeta meta = icon.getItemMeta();
    if (meta != null) {
      org.leralix.tan.utils.text.ComponentUtil.setDisplayName(meta, "§a" + getName());
      List<String> description = getBaseDescription(langType);
      if (isOwned())
        description.add(
            Lang.SPECIFIC_LANDMARK_ICON_DESC2_OWNER.get(langType, getOwner().getName()));
      else description.add(Lang.SPECIFIC_LANDMARK_ICON_DESC2_NO_OWNER.get(langType));

      org.leralix.tan.utils.text.ComponentUtil.setLore(meta, description);
    }
    icon.setItemMeta(meta);
    return icon;
  }

  public List<String> getBaseDescription(LangType langType) {
    List<String> description = new ArrayList<>();
    description.add(
        Lang.DISPLAY_COORDINATES.get(
            langType,
            Integer.toString(position.getX()),
            Integer.toString(position.getY()),
            Integer.toString(position.getZ())));
    description.add(
        Lang.SPECIFIC_LANDMARK_ICON_DESC1.get(
            langType, Integer.toString(amount), materialName.toLowerCase()));
    return description;
  }

  public void deleteLandmark() {
    dispawnChest();
    NewClaimedChunkStorage.getInstance().unclaimChunk(position.getLocation().getChunk());
    LandmarkStorage.getInstance().deleteAsync(getID()).join();
  }

  public int computeStoredReward(TownData townData) {
    TerritoryStats territoryStats = townData.getNewLevel();
    LandmarkBonus bonus = territoryStats.getStat(LandmarkBonus.class);
    return (int) bonus.multiply((double) this.amount * storedDays);
  }

  public void giveToPlayer(Player player, int number) {
    if (storedDays == 0) return;

    player.getInventory().addItem(new ItemStack(Material.valueOf(materialName), number));
    storedDays = 0;
  }

  public void setReward(ItemStack itemOnCursor) {
    this.amount = itemOnCursor.getAmount();
    this.materialName = itemOnCursor.getType().name();
    // Save this landmark to database (DatabaseStorage auto-saves on put)
    LandmarkStorage.getInstance().putAsync(this.ID, this).join();
  }

  public Location getLocation() {
    return new Location(position.getWorld(), position.getX(), position.getY(), position.getZ());
  }

  public boolean isEncircledBy(TownData playerTown) {
    Chunk chunk = position.getLocation().getChunk();

    boolean isEncircled = true;
    for (int x = -1; x <= 1; x++) {
      for (int z = -1; z <= 1; z++) {
        if (x == 0 && z == 0) continue; // Skip the center chunk
        Chunk neighborChunk = chunk.getWorld().getChunkAt(chunk.getX() + x, chunk.getZ() + z);
        ClaimedChunk2 neighborClaimedChunk =
            NewClaimedChunkStorage.getInstance().get(neighborChunk);
        if (neighborClaimedChunk instanceof WildernessChunk
            || !neighborClaimedChunk.getOwner().equals(playerTown)) {
          isEncircled = false;
          break;
        }
      }
      if (!isEncircled) break;
    }
    return isEncircled;
  }

  public void setProtectedBlockData() {
    if (getChest().isEmpty()) {
      return;
    }
    TANCustomNBT.setBockMetaData(getChest().get(), "LandmarkChest", getID());
  }

  public boolean isOwnedBy(TerritoryData territoryData) {
    if (territoryData == null) {
      return false;
    }
    if (ownerID == null) {
      return false;
    }
    return ownerID.equals(territoryData.getID());
  }
}
