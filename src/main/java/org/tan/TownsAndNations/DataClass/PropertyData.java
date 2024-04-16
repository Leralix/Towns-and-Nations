package org.tan.TownsAndNations.DataClass;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.utils.EconomyUtil;
import org.tan.TownsAndNations.utils.HeadUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PropertyData {
    private final String ID;

    private String structureID;
    private String owningPlayerID;
    private String rentingPlayerID;

    private final Vector3D p1;
    private final Vector3D p2;

    private String name;
    private String description;
    private boolean isForSale;
    private int salePrice;
    private boolean isForRent;
    private int rentPrice;



    public PropertyData(String ID, Vector3D p1, Vector3D p2, PlayerData player) {
        this.ID = ID;
        this.owningPlayerID = player.getID();
        this.p1 = p1;
        this.p2 = p2;

        this.name = "Unnamed Zone";
        this.description = "No description";
        this.isForSale = false;
        this.salePrice = 0;
        this.isForRent = false;
        this.rentingPlayerID = null;
        this.rentPrice = 0;

    }

    public String getID() {
        return ID;
    }
    public String getOwnerID() {
        return owningPlayerID;
    }
    public PlayerData getOwner() {
        return PlayerDataStorage.get(owningPlayerID);
    }
    public void sellZone(Player buyer){
        PlayerData buyerData = PlayerDataStorage.get(buyer.getUniqueId().toString());
        EconomyUtil.removeFromBalance(buyer, salePrice);

        OfflinePlayer seller = Bukkit.getOfflinePlayer(UUID.fromString(getOwnerID()));
        EconomyUtil.addFromBalance(seller, salePrice);

        owningPlayerID = buyer.getUniqueId().toString();
    }
    public void allocateRenter(Player renter){
        rentingPlayerID = renter.getUniqueId().toString();
        payRent();
    }
    public boolean isRented(){
        return rentingPlayerID != null;
    }
    public boolean isForRent(){
        return isForRent;
    }
    public PlayerData getRenter(){
        return PlayerDataStorage.get(rentingPlayerID);
    }

    public String getDescription(){
        return description;
    }

    public void payRent(){
        OfflinePlayer renter = Bukkit.getOfflinePlayer(UUID.fromString(rentingPlayerID));
        EconomyUtil.removeFromBalance(renter, rentPrice);
        OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(owningPlayerID));
        EconomyUtil.addFromBalance(owner, rentPrice);
    }
    public boolean containsBloc(Block block){
        return containsLocation(block.getLocation());
    }
    public boolean isOwner(String playerID){
        return playerID.equals(this.owningPlayerID);
    }

    public String getName(){
        return name;
    }
    public boolean isForSale(){
        return this.isForSale;
    }
    public int getRentPrice(){
        return this.rentPrice;
    }

    public boolean containsLocation(Location location) {
        return Math.max(p1.getX(),p2.getX()) >= location.getX() && Math.min(p1.getX(),p2.getX()) <= location.getX() &&
                Math.max(p1.getY(),p2.getY()) >= location.getY() && Math.min(p1.getY(),p2.getY()) <= location.getY() &&
                Math.max(p1.getZ(),p2.getZ()) >= location.getZ() && Math.min(p1.getZ(),p2.getZ()) <= location.getZ();
    }


    public ItemStack getIcon() {
        ItemStack property = HeadUtils.makeSkull(getName(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzhkYTZmY2M1Y2YzMWM2ZjcyYTAzNGI2MjBhODM3ZjlkMWM5ZWVkMzY3MTE4MmI2OTQ4OTY4N2FkYmNkOGZiIn19fQ==");

        ItemMeta meta = property.getItemMeta();


        if (meta != null) {
            List<String> lore = new ArrayList<>();

            lore.add(Lang.GUI_PROPERTY_DESCRIPTION.get(getDescription()));

            lore.add(Lang.GUI_PROPERTY_OWNER.get(getOwner().getName()));
            if(isForSale())
                lore.add(Lang.GUI_PROPERTY_FOR_SALE.get(String.valueOf(salePrice)));
            else if(isRented())
                lore.add(Lang.GUI_PROPERTY_RENTED_BY.get(getRenter().getName(), String.valueOf(rentPrice)));
            else if(isForRent())
                lore.add(Lang.GUI_PROPERTY_FOR_RENT.get(String.valueOf(rentPrice)));
            else{
                lore.add(Lang.GUI_PROPERTY_NOT_FOR_SALE.get());
            }
            meta.setLore(lore);
            property.setItemMeta(meta);
        }

        return property;
    }
}
