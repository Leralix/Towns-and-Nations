package org.tan.TownsAndNations.DataClass;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.CustomNBT;

import java.util.ArrayList;
import java.util.List;

public class Landmark {

    private final String ID;
    private final Vector3D position;
    private String materialName;
    private int amount;
    private String ownerID;

    public Landmark(String ID, Vector3D position){
        this.ID = ID;
        this.position = position;
        this.materialName = "DIAMOND";
        this.amount = 2;

        spawnChest();
    }

    public String getID(){
        return this.ID;
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

    public void ModifyReward(ItemStack newReward){
        this.materialName = newReward.getType().name();
        this.amount = newReward.getAmount();
    }

    public Vector3D getPosition(){
        return position;
    }

    public void spawnChest(){
        Block newBlock = position.getWorld().getBlockAt(position.getLocation());
        newBlock.setType(Material.CHEST);
        CustomNBT.setBockMetaData(newBlock, "LandmarkChest", getID());
    }

    public Block getChest(){
        return position.getWorld().getBlockAt(position.getLocation());
    }

    public Material getRessourceMaterial(){
        return Material.valueOf(materialName);
    }
    public ItemStack getRessources(){
        ItemStack ressourcesItemStack = new ItemStack(getRessourceMaterial());
        ressourcesItemStack.setAmount(amount);
        return ressourcesItemStack;
    }

    public void generateRessources(){
        if(!hasOwner()){
            return;
        }
        Chest chest = (Chest) getChest();
        chest.getBlockInventory().addItem(getRessources());
    }

    public boolean hasOwner() {
        return ownerID != null;
    }

    private TownData getOwner(){
        return TownDataStorage.get(ownerID);
    }


    public ItemStack getIcon() {
        Material material = Material.valueOf(materialName);
        ItemStack icon = new ItemStack(material, amount);
        ItemMeta meta =  icon.getItemMeta();
        if(meta != null) {
            meta.setDisplayName(Lang.ADMIN_GUI_SPECIFIC_LANDMARK_ICON.get(getID()));
            List<String> description = new ArrayList<>();
            description.add(Lang.ADMIN_GUI_SPECIFIC_LANDMARK_ICON_DESC1.get(amount, material.name().toLowerCase()));
            meta.setLore(description);
        }
        icon.setItemMeta(meta);
        return icon;
    }


}
