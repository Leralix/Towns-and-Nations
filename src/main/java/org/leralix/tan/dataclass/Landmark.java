package org.leralix.tan.dataclass;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.lib.data.Vector3D;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.CustomNBT;
import org.leralix.tan.utils.TerritoryUtil;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.TownsAndNations;

import java.util.ArrayList;
import java.util.List;

public class Landmark {

    private final String ID;
    private String name;
    private final Vector3D position;
    private String materialName;
    private int amount;
    private String ownerID;
    private int storedDays;

    public Landmark(String id, Vector3D position){
        this.ID = id;
        this.name = Lang.SPECIFIC_LANDMARK_ICON_DEFAULT_NAME.get(getID());
        this.position = position;
        this.materialName = "DIAMOND";
        this.amount = 2;
        this.storedDays = 0;
        spawnChest();
    }

    public String getID(){
        return this.ID;
    }

    public String getName(){
        return name;
    }
    public void setName(String newName){
        this.name = newName;
    }

    public void setOwnerID(TownData newOwner){
        setOwnerID(newOwner.getID());
    }
    public void setOwnerID(String newOwnerID){
        this.ownerID = newOwnerID;
    }
    public void clearOwner() {
        this.ownerID = null;
    }
    public String getOwnerID() {
        return ownerID;
    }

    public Vector3D getPosition(){
        return position;
    }

    public void spawnChest(){
        Block newBlock = position.getWorld().getBlockAt(position.getLocation());
        newBlock.setType(Material.CHEST);
        CustomNBT.setBockMetaData(newBlock, "LandmarkChest", getID());
    }

    public void dispawnChest(){
        Block newBlock = position.getWorld().getBlockAt(position.getLocation());
        newBlock.removeMetadata("LandmarkChest", TownsAndNations.getPlugin());
        newBlock.setType(Material.AIR);
    }

    public Block getChest(){
        return position.getWorld().getBlockAt(position.getLocation());
    }

    public Material getRessourceMaterial(){
        return Material.valueOf(materialName);
    }
    @SuppressWarnings("unused")
    public ItemStack getRessources(){
        ItemStack ressourcesItemStack = new ItemStack(getRessourceMaterial());
        ressourcesItemStack.setAmount(amount);
        return ressourcesItemStack;
    }

    public void generateRessources(){
        if(!hasOwner())
            return;
        if(storedDays > 7)
            return;
        storedDays++;

    }

    public boolean hasOwner() {
        if(ownerID == null)
            return false;
        if(TerritoryUtil.getTerritory(ownerID) == null){
            clearOwner();
            return false;
        }
        return true;
    }

    private TownData getOwner(){
        return TownDataStorage.get(ownerID);
    }


    public ItemStack getIcon() {
        Material material = Material.valueOf(materialName);
        ItemStack icon = new ItemStack(material, amount);
        ItemMeta meta =  icon.getItemMeta();
        if(meta != null) {
            meta.setDisplayName(ChatColor.GREEN + getName());
            List<String> description = new ArrayList<>();
            description.add(Lang.DISPLAY_COORDINATES.get(position.getX(), position.getY(), position.getZ()));
            description.add(Lang.SPECIFIC_LANDMARK_ICON_DESC1.get(amount, material.name().toLowerCase()));
            if(hasOwner())
                description.add(Lang.SPECIFIC_LANDMARK_ICON_DESC2_OWNER.get(getOwner().getName()));
            else
                description.add(Lang.SPECIFIC_LANDMARK_ICON_DESC2_NO_OWNER.get());

            meta.setLore(description);
        }
        icon.setItemMeta(meta);
        return icon;
    }

    public void deleteLandmark(){
        dispawnChest();
        if(hasOwner())
            getOwner().removeLandmark(getID());
        NewClaimedChunkStorage.unclaimChunk(position.getLocation().getChunk());
        LandmarkStorage.getLandMarkMap().remove(getID());

    }

    public int computeStoredReward(TownData townData){
        long bonus = (townData.getLevel().getTotalBenefits().get("LANDMARK_BONUS") + 100 ) /100;
        return (int) (this.amount * storedDays * bonus);
    }

    public void giveToPlayer(Player player, int number){
        if(storedDays == 0)
            return;

        player.getInventory().addItem(new ItemStack(Material.valueOf(materialName), number));
        storedDays = 0;
    }


    public void setReward(ItemStack itemOnCursor) {
        this.amount = itemOnCursor.getAmount();
        this.materialName = itemOnCursor.getType().name();
        LandmarkStorage.save();
    }

    public Location getLocation() {
        return new Location(position.getWorld(), position.getX(), position.getY(), position.getZ());
    }

}
