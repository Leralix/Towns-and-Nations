package org.leralix.tan.dataclass;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.lib.position.Vector3D;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.WildernessChunk;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.TANCustomNBT;
import org.leralix.tan.utils.TerritoryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        this.name = Lang.SPECIFIC_LANDMARK_ICON_DEFAULT_NAME.get(getID());
        this.position = position;
        this.materialName = "DIAMOND";
        this.amount = 2;
        this.storedDays = 0;
        this.storedLimit = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("storedLimit", 7);
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
        setOwnerID(newOwner.getID());
    }

    private void setOwnerID(String newOwnerID) {
        this.ownerID = newOwnerID;
    }

    public void removeOwnership() {
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

    private TownData getOwner() {
        return TownDataStorage.getInstance().get(ownerID);
    }


    public ItemStack getIcon() {
        Material rewardMaterial = Material.valueOf(materialName);
        ItemStack icon = new ItemStack(rewardMaterial, amount);
        ItemMeta meta = icon.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + getName());
            List<String> description = getBaseDescription(rewardMaterial);
            if (isOwned())
                description.add(Lang.SPECIFIC_LANDMARK_ICON_DESC2_OWNER.get(getOwner().getName()));
            else
                description.add(Lang.SPECIFIC_LANDMARK_ICON_DESC2_NO_OWNER.get());

            meta.setLore(description);
        }
        icon.setItemMeta(meta);
        return icon;
    }

    public List<String> getBaseDescription(Material material) {
        List<String> description = new ArrayList<>();
        description.add(Lang.DISPLAY_COORDINATES.get(position.getX(), position.getY(), position.getZ()));
        description.add(Lang.SPECIFIC_LANDMARK_ICON_DESC1.get(amount, material.name().toLowerCase()));
        return description;
    }

    public void deleteLandmark() {
        dispawnChest();
        if (isOwned())
            getOwner().removeLandmark(getID());
        NewClaimedChunkStorage.getInstance().unclaimChunk(position.getLocation().getChunk());
        LandmarkStorage.getInstance().delete(getID());

    }

    public int computeStoredReward(TownData townData) {
        long bonus = (townData.getLevel().getTotalBenefits().get("LANDMARK_BONUS") + 100) / 100;
        return (int) (this.amount * storedDays * bonus);
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

    public boolean isEncircledBy(TownData playerTown) {
        Chunk chunk = position.getLocation().getChunk();

        boolean isEncircled = true;
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) continue; // Skip the center chunk
                Chunk neighborChunk = chunk.getWorld().getChunkAt(chunk.getX() + x, chunk.getZ() + z);
                ClaimedChunk2 neighborClaimedChunk = NewClaimedChunkStorage.getInstance().get(neighborChunk);
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
}
