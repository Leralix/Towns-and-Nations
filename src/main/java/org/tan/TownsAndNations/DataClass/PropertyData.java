package org.tan.TownsAndNations.DataClass;

import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.enums.SoundEnum;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PropertyData {
    private final String ID;

    private String owningPlayerID;
    private String rentingPlayerID;

    private String name;
    private String description;
    private boolean isForSale;
    private int salePrice;
    private boolean isForRent;
    private int rentPrice;

    private final Vector3D p1;
    private final Vector3D p2;
    private Vector3D signLocation;

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
    public void setSignLocation(Location loc){
        this.signLocation = new Vector3D(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getBlock().getWorld().getUID().toString());
    }


    public Vector3D getP1(){
        return this.p1;
    }
    public Vector3D getP2(){
        return this.p2;
    }

    public String getTotalID() {
        return ID;
    }
    public String getOwningStructureID(){
        String[] parts = ID.split("_");
        return parts[0];
    }
    public String getPropertyID(){
        String[] parts = ID.split("_");
        return parts[1];
    }
    public void setName(String name) {
        this.name = name;

        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), this::updateSign);

    }

    public void setDescription(String description) {
        this.description = description;
        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), this::updateSign);
    }
    public String getOwnerID() {
        return owningPlayerID;
    }
    public String getRentingPlayerID() {
        return rentingPlayerID;
    }
    public PlayerData getOwner() {
        return PlayerDataStorage.get(owningPlayerID);
    }
    public void sellZone(Player buyer){
        EconomyUtil.removeFromBalance(buyer, salePrice);

        OfflinePlayer seller = Bukkit.getOfflinePlayer(UUID.fromString(getOwnerID()));
        EconomyUtil.addFromBalance(seller, salePrice);

        owningPlayerID = buyer.getUniqueId().toString();
    }
    public void allocateRenter(Player renter){
        rentingPlayerID = renter.getUniqueId().toString();
        payRent();
        this.isForRent = false;
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
            lore.add(Lang.GUI_PROPERTY_STRUCTURE_OWNER.get(TownDataStorage.get(getOwningStructureID()).getName()));

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

            lore.add(Lang.CLICK_TO_OPEN_PROPERTY_MENU.get());
            meta.setLore(lore);
            property.setItemMeta(meta);
        }

        return property;
    }

    public Object getBuyingPrice() {
        return this.salePrice;
    }

    public void swapIsForSale() {
        this.isForSale = !this.isForSale;
        if(this.isForSale)
            this.isForRent = false;
        updateSign();
    }
    public void swapIsRent() {
        this.isForRent = !this.isForRent;
        if(this.isForRent)
            this.isForSale = false;
        updateSign();
    }

    public boolean isNearProperty(Location blockLocation, int margin) {
        double minX = Math.min(this.p1.getX(), this.p2.getX()) - margin;
        double minY = Math.min(this.p1.getY(), this.p2.getY()) - margin;
        double minZ = Math.min(this.p1.getZ(), this.p2.getZ()) - margin;
        double maxX = Math.max(this.p1.getX(), this.p2.getX()) + margin;
        double maxY = Math.max(this.p1.getY(), this.p2.getY()) + margin;
        double maxZ = Math.max(this.p1.getZ(), this.p2.getZ()) + margin;

        double blockX = blockLocation.getX();
        double blockY = blockLocation.getY();
        double blockZ = blockLocation.getZ();

        return blockX >= minX && blockX <= maxX &&
                blockY >= minY && blockY <= maxY &&
                blockZ >= minZ && blockZ <= maxZ;
    }

    public void showBox(Player player) {
        ParticleUtils.showBox(player,this.getP1(),this.getP2(), 10);
    }
    public void updateSign(){
        World world = Bukkit.getWorld(signLocation.getWorldID());
        if(world == null)
            return;
        Block signBlock = world.getBlockAt(signLocation.getX(), signLocation.getY(), signLocation.getZ());

        Sign sign = (Sign) signBlock.getState();

            String[] lines = updateLines();

            sign.setLine(0, lines[0]);
            sign.setLine(1, lines[1]);
            sign.setLine(2, lines[2]);
            sign.setLine(3, lines[3]);

            // Mettre à jour la pancarte
            sign.update();
        }

    private String[] updateLines() {

        String[] lines = new String[4];

        lines[0] = Lang.SIGN_NAME.get(this.getName());
        lines[1] = Lang.SIGN_PLAYER.get(PlayerDataStorage.get(this.getOwnerID()).getName());

        if(this.isForSale) {
            lines[2] = Lang.SIGN_FOR_SALE.get();
            lines[3] = Lang.SIGN_SALE_PRICE.get(this.getBuyingPrice());
        } else if(this.isForRent) {
            lines[2] = Lang.SIGN_RENT.get();
            lines[3] = Lang.SIGN_RENT_PRICE.get(this.getRentPrice());
        } else if(this.isRented()) {
            lines[2] = Lang.SIGN_RENTED_BY.get(this.getRenter().getName().substring(0, Math.min(this.getRenter().getName().length(), 7)));
            lines[3] = Lang.SIGN_RENT_PRICE.get(this.getRentPrice());
        } else {
            lines[2] = Lang.SIGN_NOT_FOR_SALE.get();
            lines[3] = "";
        }

        return lines;
    }

    public void setRentPrice(int i) {
        this.rentPrice = i;
        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), this::updateSign);
    }

    public void setSalePrice(int i) {
        this.salePrice = i;
        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), this::updateSign);
    }

    public Block getSignLocation() {
        return Bukkit.getWorld(signLocation.getWorldID()).getBlockAt(this.signLocation.getX(), this.signLocation.getY(), this.signLocation.getZ());
    }

    public void delete() {
        PlayerData Owner = PlayerDataStorage.get(owningPlayerID);
        TownData town = TownDataStorage.get(Owner.getTownId());
        town.removeProperty(this);
        Owner.removeProperty(this);
        removeSign();

        Player player = Bukkit.getPlayer(UUID.fromString(owningPlayerID));
        if (player != null){
            player.sendMessage(ChatUtils.getTANString() + Lang.PROPERTY_DELETED.get());
            SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
        }


    }

    private void removeSign() {
        World world = Bukkit.getWorld(signLocation.getWorldID());
        Block signBlock = world.getBlockAt(signLocation.getX(), signLocation.getY(), signLocation.getZ());
        signBlock.setType(org.bukkit.Material.AIR);
        world.spawnParticle(Particle.BUBBLE_POP, signBlock.getLocation(), 5);
    }

    public void bought(Player player) {

    }
}