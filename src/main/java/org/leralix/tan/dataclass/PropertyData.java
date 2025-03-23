package org.leralix.tan.dataclass;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.position.Vector3D;
import org.leralix.lib.utils.ParticleUtils;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.newhistory.PropertyBuyTaxTransaction;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.territory.TownData;
import org.tan.api.interfaces.TanProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PropertyData {
    private final String ID;
    private String owningPlayerID;
    private String rentingPlayerID;
    private List<String> allowedPlayers;
    private String name;
    private String description;
    private boolean isForSale;
    private double salePrice;
    private boolean isForRent;
    private double rentPrice;
    private final Vector3D p1;
    private final Vector3D p2;
    private Vector3D signLocation;

    public PropertyData(String id, Vector3D p1, Vector3D p2, PlayerData player) {
        this.ID = id;
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

        this.allowedPlayers = new ArrayList<>();
    }
    public void setSignLocation(Location loc){
        this.signLocation = new Vector3D(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getBlock().getWorld().getUID().toString());
    }


    public Vector3D getFirstCorner(){
        return this.p1;
    }
    public Vector3D getSecondCorner(){
        return this.p2;
    }

    public String getTotalID() {
        return ID;
    }
    private String getOwningStructureID(){
        String[] parts = ID.split("_");
        return parts[0];
    }
    public TownData getTerritory(){
        return TownDataStorage.getInstance().get(getOwningStructureID());
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
    public PlayerData getOwner() {
        return PlayerDataStorage.getInstance().get(owningPlayerID);
    }

    public void allocateRenter(Player renter){
        rentingPlayerID = renter.getUniqueId().toString();
        this.isForRent = false;
        if(ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("payRentAtStart", false))
            payRent();
        this.updateSign();
        getAllowedPlayersID().clear();
    }
    public boolean isRented(){
        return rentingPlayerID != null;
    }
    public boolean isForRent(){
        return isForRent;
    }
    public PlayerData getRenter(){
        return PlayerDataStorage.getInstance().get(rentingPlayerID);
    }
    public String getRenterID(){
        return rentingPlayerID;
    }
    public Player getRenterPlayer(){
        return Bukkit.getPlayer(UUID.fromString(rentingPlayerID));
    }
    public OfflinePlayer getOfflineRenter(){
        return Bukkit.getOfflinePlayer(UUID.fromString(rentingPlayerID));
    }
    public Player getOwnerPlayer() {
        return Bukkit.getPlayer(UUID.fromString(owningPlayerID));
    }
    public String getDescription(){
        return description;
    }
    public void payRent(){
        OfflinePlayer renter = Bukkit.getOfflinePlayer(UUID.fromString(rentingPlayerID));
        OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(owningPlayerID));
        TerritoryData town = getTerritory();

        double tax = rentPrice * town.getTaxOnRentingProperty();


        if(EconomyUtil.getBalance(renter) < rentPrice){
            expelRenter(true);
            return;
        }


        EconomyUtil.removeFromBalance(renter, rentPrice);
        EconomyUtil.addFromBalance(owner, rentPrice - tax);
        town.addToBalance(tax);

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
    public double getRentPrice(){
        return this.rentPrice;
    }

    public boolean containsLocation(Location location) {
        return Math.max(p1.getX(),p2.getX()) >= location.getX() && Math.min(p1.getX(),p2.getX()) <= location.getX() &&
                Math.max(p1.getY(),p2.getY()) >= location.getY() && Math.min(p1.getY(),p2.getY()) <= location.getY() &&
                Math.max(p1.getZ(),p2.getZ()) >= location.getZ() && Math.min(p1.getZ(),p2.getZ()) <= location.getZ();
    }


    public ItemStack getIcon(LangType langType) {
        ItemStack property = HeadUtils.makeSkullB64(getName(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzhkYTZmY2M1Y2YzMWM2ZjcyYTAzNGI2MjBhODM3ZjlkMWM5ZWVkMzY3MTE4MmI2OTQ4OTY4N2FkYmNkOGZiIn19fQ==");
        ItemMeta meta = property.getItemMeta();

        if (meta != null) {
            List<String> lore = new ArrayList<>();

            lore.add(Lang.GUI_PROPERTY_DESCRIPTION.get(langType, getDescription()));
            lore.add(Lang.GUI_PROPERTY_STRUCTURE_OWNER.get(langType, getTerritory().getName()));

            lore.add(Lang.GUI_PROPERTY_OWNER.get(langType, getOwner().getNameStored()));
            if(isForSale())
                lore.add(Lang.GUI_PROPERTY_FOR_SALE.get(langType, String.valueOf(salePrice)));
            else if(isRented())
                lore.add(Lang.GUI_PROPERTY_RENTED_BY.get(langType, getRenter().getNameStored(), String.valueOf(rentPrice)));
            else if(isForRent())
                lore.add(Lang.GUI_PROPERTY_FOR_RENT.get(langType, String.valueOf(rentPrice)));
            else{
                lore.add(Lang.GUI_PROPERTY_NOT_FOR_SALE.get(langType));
            }

            lore.add(Lang.CLICK_TO_OPEN_PROPERTY_MENU.get(langType));
            meta.setLore(lore);
            property.setItemMeta(meta);
        }

        return property;
    }

    public double getSalePrice() {
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
        double minX = (double) Math.min(this.p1.getX(), this.p2.getX()) - margin;
        double minY = (double) Math.min(this.p1.getY(), this.p2.getY()) - margin;
        double minZ = (double) Math.min(this.p1.getZ(), this.p2.getZ()) - margin;
        double maxX = (double) Math.max(this.p1.getX(), this.p2.getX()) + margin;
        double maxY = (double) Math.max(this.p1.getY(), this.p2.getY()) + margin;
        double maxZ = (double) Math.max(this.p1.getZ(), this.p2.getZ()) + margin;

        double blockX = blockLocation.getX();
        double blockY = blockLocation.getY();
        double blockZ = blockLocation.getZ();

        return blockX >= minX && blockX <= maxX &&
                blockY >= minY && blockY <= maxY &&
                blockZ >= minZ && blockZ <= maxZ;
    }

    public void showBox(Player player) {
        ParticleUtils.showBox(TownsAndNations.getPlugin(), player,this.getFirstCorner(),this.getSecondCorner(), 10);
    }
    public void updateSign(){
        World world = Bukkit.getWorld(signLocation.getWorldID());
        if(world == null)
            return;
        Block signBlock = world.getBlockAt(signLocation.getX(), signLocation.getY(), signLocation.getZ());

        Sign sign = (Sign) signBlock.getState();

        String[] lines = updateLines();

        SignSide signSide = sign.getSide(Side.FRONT);

        signSide.setLine(0, lines[0]);
        signSide.setLine(1, lines[1]);
        signSide.setLine(2, lines[2]);
        signSide.setLine(3, lines[3]);

        sign.update();
    }

    private String[] updateLines() {

        String[] lines = new String[4];

        lines[0] = Lang.SIGN_NAME.get(this.getName());
        lines[1] = Lang.SIGN_PLAYER.get(PlayerDataStorage.getInstance().get(this.getOwnerID()).getNameStored());

        if(this.isForSale) {
            lines[2] = Lang.SIGN_FOR_SALE.get();
            lines[3] = Lang.SIGN_SALE_PRICE.get(this.getSalePrice());
        } else if(this.isForRent) {
            lines[2] = Lang.SIGN_RENT.get();
            lines[3] = Lang.SIGN_RENT_PRICE.get(this.getRentPrice());
        } else if(this.isRented()) {
            lines[2] = Lang.SIGN_RENTED_BY.get();
            lines[3] = this.getRenter().getNameStored();
        } else {
            lines[2] = Lang.SIGN_NOT_FOR_SALE.get();
            lines[3] = "";
        }

        return lines;
    }

    public void setRentPrice(double i) {
        this.rentPrice = i;
        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), this::updateSign);
    }

    public void setSalePrice(double i) {
        this.salePrice = i;
        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), this::updateSign);
    }

    public Block getSign() {
        return Bukkit.getWorld(signLocation.getWorldID()).getBlockAt(this.signLocation.getX(), this.signLocation.getY(), this.signLocation.getZ());
    }



    public void delete() {
        PlayerData owner = PlayerDataStorage.getInstance().get(owningPlayerID);
        TownData town = getTerritory();
        expelRenter(false);
        removeSign();

        owner.removeProperty(this);
        town.removeProperty(this);

        Player player = Bukkit.getPlayer(UUID.fromString(owningPlayerID));
        if (player != null){
            player.sendMessage(TanChatUtils.getTANString() + Lang.PROPERTY_DELETED.get());
            SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
        }


    }

    private void removeSign() {
        World world = Bukkit.getWorld(signLocation.getWorldID());
        Block signBlock = world.getBlockAt(signLocation.getX(), signLocation.getY(), signLocation.getZ());
        signBlock.setType(org.bukkit.Material.AIR);
        world.spawnParticle(Particle.BUBBLE_POP, signBlock.getLocation(), 5);
    }

    public void buyProperty(Player player) {

        double playerBalance = EconomyUtil.getBalance(player);
        if(playerBalance < salePrice){
            player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.get(salePrice - playerBalance));
            SoundUtil.playSound(player, SoundEnum.MINOR_BAD);
            return;
        }

        Player exOwner = Bukkit.getPlayer(UUID.fromString(owningPlayerID));
        OfflinePlayer exOwnerOffline = Bukkit.getOfflinePlayer(UUID.fromString(owningPlayerID));

        if(exOwner != null){
            exOwner.sendMessage(TanChatUtils.getTANString() + Lang.PROPERTY_SOLD_EX_OWNER.get(getName(),player.getName(), getSalePrice()));
            SoundUtil.playSound(exOwner, SoundEnum.GOOD);
        }
        player.sendMessage(TanChatUtils.getTANString() + Lang.PROPERTY_SOLD_NEW_OWNER.get(getName(), getSalePrice()));
        SoundUtil.playSound(player, SoundEnum.GOOD);

        TownData town = getTerritory();
        double tax = salePrice * getTerritory().getTaxOnBuyingProperty();
        TownsAndNations.getPlugin().getDatabaseHandler().addTransactionHistory(new PropertyBuyTaxTransaction(town, this, tax));

        EconomyUtil.removeFromBalance(player, salePrice);
        EconomyUtil.addFromBalance(exOwnerOffline, salePrice - tax);
        town.addToBalance(tax);

        PlayerData exOwnerData = PlayerDataStorage.getInstance().get(owningPlayerID);
        PlayerData newOwnerData = PlayerDataStorage.getInstance().get(player.getUniqueId().toString());

        exOwnerData.removeProperty(this);
        newOwnerData.addProperty(this);


        this.owningPlayerID = player.getUniqueId().toString();

        this.isForSale = false;
        updateSign();
        getAllowedPlayersID().clear();

    }
    public List<String> getAllowedPlayersID(){
        if(allowedPlayers == null)
            allowedPlayers = new ArrayList<>();
        return allowedPlayers;
    }

    public boolean isPlayerAllowed(PlayerData playerData) {
        if(getAllowedPlayersID().contains(playerData.getID()))
            return true;
        if(isRented())
            return playerData.getID().equals(rentingPlayerID);
        return isOwner(playerData.getID());
    }

    public String getDenyMessage() {
        if(isRented())
            return Lang.PROPERTY_RENTED_BY.get(getRenter().getNameStored());
        else
            return Lang.PROPERTY_BELONGS_TO.get(getOwner().getNameStored());

    }

    public void expelRenter(boolean rentBack) {
        if(!isRented())
            return;
        PlayerData renter = PlayerDataStorage.getInstance().get(rentingPlayerID);
        renter.removeProperty(this);
        this.rentingPlayerID = null;
        if(rentBack)
            isForRent = true;
        updateSign();
        getAllowedPlayersID().clear();
    }

    public void addAuthorizedPlayer(Player player){
        addAuthorizedPlayer(player.getUniqueId().toString());
    }
    public void addAuthorizedPlayer(String playerID){
        getAllowedPlayersID().add(playerID);
    }

    public void removeAuthorizedPlayer(String playerID){
        getAllowedPlayersID().remove(playerID);
    }


    public boolean canPlayerManageInvites(String id) {
        if(!isRented() && isOwner(id))
            return true;
        return isRented() && Objects.equals(getRenterID(), id);
    }

    public boolean isPlayerAuthorized(Player playerIter) {
        return isPlayerAuthorized(playerIter.getUniqueId().toString());
    }

    public boolean isPlayerAuthorized(String playerID) {
        return getAllowedPlayersID().contains(playerID);
    }

    public boolean isInChunk(ClaimedChunk2 chunk) {
        int minX = Math.min(p1.getX() >> 4, p2.getX() >> 4);
        int maxX = Math.max(p1.getX() >> 4, p2.getX() >> 4);
        int minZ = Math.min(p1.getZ() >> 4, p2.getZ() >> 4);
        int maxZ = Math.max(p1.getZ() >> 4, p2.getZ() >> 4);
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        return (chunkX >= minX && chunkX <= maxX && chunkZ >= minZ && chunkZ <= maxZ);
    }
}