package org.leralix.tan.data.building.landmark;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.chunk.IClaimedChunk;
import org.leralix.tan.data.chunk.TerritoryChunk;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.data.upgrade.TerritoryStats;
import org.leralix.tan.data.upgrade.rewards.percentage.LandmarkBonus;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.LandmarkClaimedInternalEvent;
import org.leralix.tan.events.events.LandmarkUnclaimedInternalEvent;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.gameplay.TANCustomNBT;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.territory.ChunkUtil;
import org.tan.api.interfaces.buildings.TanLandmark;
import org.tan.api.interfaces.territory.TanTerritory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LandmarkData implements Landmark, TanLandmark {

    private final String ID;
    private String name;
    private final Vector3D position;
    private String materialName;
    private int amount;
    private String ownerID;
    private int storedDays;
    private int storedLimit;

    public LandmarkData(String id, Vector3D position) {
        this.ID = id;
        this.name = Lang.SPECIFIC_LANDMARK_ICON_DEFAULT_NAME.get(Lang.getServerLang(), getID());
        this.position = position;
        this.materialName = "DIAMOND";
        this.amount = 2;
        this.storedDays = 0;
        this.storedLimit = Constants.getLandmarkStorageCapacity();
        spawnChest();
    }

    @Override
    public String getID() {
        return this.ID;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String newName) {
        this.name = newName;
    }

    @Override
    public void setOwner(Town newOwner) {
        EventManager.getInstance().callEvent(new LandmarkClaimedInternalEvent(this, newOwner));
        this.ownerID = newOwner.getID();
    }

    @Override
    public void setOwner(TanTerritory newOwner) {
        if (newOwner instanceof Town townData) {
            setOwner(townData);
        }
    }

    @Override
    public void removeOwnership() {
        EventManager.getInstance().callEvent(new LandmarkUnclaimedInternalEvent(this, getOwner()));
        this.ownerID = null;
    }

    @Override
    public String getOwnerID() {
        return ownerID;
    }

    @Override
    public Vector3D getPosition() {
        return position;
    }

    @Override
    public void spawnChest() {
        Block newBlock = position.getWorld().getBlockAt(position.getLocation());
        newBlock.setType(Material.CHEST);
        TANCustomNBT.setBockMetaData(newBlock, "LandmarkChest", getID());
    }

    @Override
    public void dispawnChest() {
        Block newBlock = position.getWorld().getBlockAt(position.getLocation());

        TANCustomNBT.removeBockMetaData(newBlock, "LandmarkChest");
        newBlock.setType(Material.AIR);
    }

    @Override
    public Optional<Block> getChest() {
        World world = position.getWorld();
        if (world == null) {
            return Optional.empty();
        }
        return Optional.of(position.getWorld().getBlockAt(position.getLocation()));
    }

    @Override
    public Material getRessourceMaterial() {
        return Material.valueOf(materialName);
    }

    @Override
    public ItemStack getResources() {
        ItemStack ressourcesItemStack = new ItemStack(getRessourceMaterial());
        ressourcesItemStack.setAmount(amount);
        return ressourcesItemStack;
    }

    @Override
    public void generateResources() {
        if (!isOwned())
            return;
        if (storedDays >= storedLimit)
            return;
        storedDays++;
    }

    @Override
    public void setStoredLimit(int limit) {
        storedLimit = limit;
    }

    @Override
    public boolean isOwned() {
        if (ownerID == null)
            return false;
        if (TerritoryUtil.getTerritory(ownerID) == null) {
            removeOwnership();
            return false;
        }
        return true;
    }

    @Override
    public TanTerritory getOwner() {
        return TownsAndNations.getPlugin().getTownStorage().get(ownerID);
    }

    @Override
    public IconBuilder getIcon(LangType langType) {

        List<FilledLang> description = getBaseDescription();
        if (isOwned())
            description.add(Lang.SPECIFIC_LANDMARK_ICON_DESC2_OWNER.get(getOwner().getName()));
        else
            description.add(Lang.SPECIFIC_LANDMARK_ICON_DESC2_NO_OWNER.get());


        return IconManager.getInstance().get(Material.valueOf(materialName))
                .setName(Lang.SPECIFIC_LANDMARK_NAME.get(langType, getName()))
                .setDescription(description);
    }

    @Override
    public List<FilledLang> getBaseDescription() {
        List<FilledLang> description = new ArrayList<>();
        description.add(Lang.DISPLAY_COORDINATES.get(Integer.toString(position.getX()), Integer.toString(position.getY()), Integer.toString(position.getZ())));
        description.add(Lang.SPECIFIC_LANDMARK_ICON_DESC1.get(Integer.toString(amount), materialName.toLowerCase()));
        return description;
    }

    @Override
    public void deleteLandmark() {
        dispawnChest();
        TownsAndNations.getPlugin().getClaimStorage().unclaimChunk(position.getLocation().getChunk());
        TownsAndNations.getPlugin().getLandmarkStorage().delete(this);

    }

    @Override
    public int computeStoredReward(Territory townData) {
        TerritoryStats territoryStats = townData.getNewLevel();
        LandmarkBonus bonus = territoryStats.getStat(LandmarkBonus.class);
        return (int) bonus.multiply((double) this.amount * storedDays);
    }

    @Override
    public void giveToPlayer(Player player, int number) {
        if (storedDays == 0)
            return;

        player.getInventory().addItem(new ItemStack(Material.valueOf(materialName), number));
        storedDays = 0;
    }

    @Override
    public void setReward(ItemStack itemOnCursor) {
        setQuantity(itemOnCursor.getAmount());
        setItem(itemOnCursor);
    }

    @Override
    public Location getLocation() {
        return new Location(position.getWorld(), position.getX(), position.getY(), position.getZ());
    }

    @Override
    public void setQuantity(int quantity) {
        this.amount = quantity;
    }

    @Override
    public int getQuantity() {
        return this.amount;
    }

    @Override
    public void setItem(ItemStack item) {
        this.materialName = item.getType().name();
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(getRessourceMaterial());
    }

    /**
     * Check if this landmark is encircled by the given territory
     *
     * @param territoryToCompare the territory to compare
     * @return true if the landmark is encircled by the given territory, false otherwise
     */
    @Override
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

    private IClaimedChunk getChunk() {
        return TownsAndNations.getPlugin().getClaimStorage().get(position.getLocation().getChunk());
    }

    @Override
    public boolean isOwnedBy(Territory territoryData) {
        if (territoryData == null) {
            return false;
        }
        if (ownerID == null) {
            return false;
        }
        return ownerID.equals(territoryData.getID());
    }
}
