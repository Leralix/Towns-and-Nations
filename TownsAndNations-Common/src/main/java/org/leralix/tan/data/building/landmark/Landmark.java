package org.leralix.tan.data.building.landmark;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.data.chunk.ClaimedChunk;
import org.leralix.tan.data.chunk.TerritoryChunk;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.data.upgrade.TerritoryStats;
import org.leralix.tan.data.upgrade.rewards.percentage.LandmarkBonus;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.LandmarkClaimedInternalEvent;
import org.leralix.tan.events.events.LandmarkUnclaimedInternalEvent;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.gameplay.TANCustomNBT;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.territory.ChunkUtil;
import org.tan.api.interfaces.TanLandmark;
import org.tan.api.interfaces.TanTerritory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Landmark implements TanLandmark {

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

    @Override
    public void setOwner(UUID newOwner) {
        TownData townData = TownDataStorage.getInstance().get(newOwner.toString());
        if(townData != null)
            setOwner(townData);
    }

    @Override
    public void setOwner(TanTerritory newOwner) {
        if(newOwner instanceof TownData townData){
            setOwner(townData);
        }
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
        if (!isOwned())
            return;
        if (storedDays >= storedLimit)
            return;
        storedDays++;
    }

    public void setStoredLimit(int limit) {
        storedLimit = limit;
    }

    public boolean isOwned() {
        if (ownerID == null)
            return false;
        if (TerritoryUtil.getTerritory(ownerID) == null) {
            removeOwnership();
            return false;
        }
        return true;
    }

    public TanTerritory getOwner() {
        return TownDataStorage.getInstance().get(ownerID);
    }


    public ItemStack getIcon(LangType langType) {
        Material rewardMaterial = Material.valueOf(materialName);
        ItemStack icon = new ItemStack(rewardMaterial, amount);
        ItemMeta meta = icon.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + getName());
            List<String> description = getBaseDescription(langType);
            if (isOwned())
                description.add(Lang.SPECIFIC_LANDMARK_ICON_DESC2_OWNER.get(langType, getOwner().getName()));
            else
                description.add(Lang.SPECIFIC_LANDMARK_ICON_DESC2_NO_OWNER.get(langType));

            meta.setLore(description);
        }
        icon.setItemMeta(meta);
        return icon;
    }

    public List<String> getBaseDescription(LangType langType) {
        List<String> description = new ArrayList<>();
        description.add(Lang.DISPLAY_COORDINATES.get(langType, Integer.toString(position.getX()), Integer.toString(position.getY()), Integer.toString(position.getZ())));
        description.add(Lang.SPECIFIC_LANDMARK_ICON_DESC1.get(langType, Integer.toString(amount), materialName.toLowerCase()));
        return description;
    }

    public void deleteLandmark() {
        dispawnChest();
        NewClaimedChunkStorage.getInstance().unclaimChunk(position.getLocation().getChunk());
        LandmarkStorage.getInstance().delete(getID());

    }

    public int computeStoredReward(TerritoryData townData) {
        TerritoryStats territoryStats = townData.getNewLevel();
        LandmarkBonus bonus = territoryStats.getStat(LandmarkBonus.class);
        return (int) bonus.multiply((double) this.amount * storedDays);
    }

    public void giveToPlayer(Player player, int number) {
        if (storedDays == 0)
            return;

        player.getInventory().addItem(new ItemStack(Material.valueOf(materialName), number));
        storedDays = 0;
    }


    public void setReward(ItemStack itemOnCursor) {
        this.amount = itemOnCursor.getAmount();
        this.materialName = itemOnCursor.getType().name();
        LandmarkStorage.getInstance().save();
    }

    public Location getLocation() {
        return new Location(position.getWorld(), position.getX(), position.getY(), position.getZ());
    }

    @Override
    public void setQuantity(int quantity) {

    }

    @Override
    public int getQuantity() {
        return 0;
    }

    @Override
    public void setItem(ItemStack item) {

    }

    @Override
    public ItemStack getItem() {
        return null;
    }

    /**
     * Check if this landmark is encircled by the given territory
     * @param territoryToCompare the territory to compare
     * @return  true if the landmark is encircled by the given territory, false otherwise
     */
    public boolean isEncircledBy(TanTerritory territoryToCompare) {

        return ChunkUtil.isChunkEncirecledBy(
                getChunk(),
                chunk -> {
                    if (chunk instanceof TerritoryChunk territoryChunk) {
                        return territoryChunk.getOwnerID().equals(territoryToCompare.getID());
                    }
                    return false;
                });
    }

    private ClaimedChunk getChunk() {
        return NewClaimedChunkStorage.getInstance().get(position.getLocation().getChunk());
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
