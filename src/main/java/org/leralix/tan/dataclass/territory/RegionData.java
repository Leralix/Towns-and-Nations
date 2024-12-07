package org.leralix.tan.dataclass.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.*;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.RegionClaimedChunk;
import org.leralix.tan.dataclass.newhistory.SubjectTaxHistory;
import org.leralix.tan.dataclass.territory.economy.Budget;
import org.leralix.tan.dataclass.territory.economy.SubjectTaxLine;
import org.leralix.tan.dataclass.wars.PlannedAttack;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.enums.SoundEnum;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.ClaimBlacklistStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.*;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import java.util.*;

public class RegionData extends TerritoryData {

    private final String id;
    private final String name;
    private String leaderID;
    private String capitalID;
    private String nationID;
    private Long dateTimeCreated;
    private String regionIconType;
    private CustomIcon territoryIcon;
    private Double taxRate;
    private Double balance;
    private String description;
    private final List<String> townsInRegion;
    private RelationData relations;

    public RegionData(String id, String name, String ownerID) {
        super(id, name);
        PlayerData owner = PlayerDataStorage.get(ownerID);
        TownData ownerTown = TownDataStorage.get(owner);

        this.id = id;
        this.name = name;
        this.capitalID = ownerTown.getID();
        this.dateTimeCreated = new Date().getTime();
        this.nationID = null;
        this.regionIconType = null;
        this.taxRate = 1.0;
        this.balance = 0.0;
        this.description = "default description";
        this.townsInRegion = new ArrayList<>();
        this.townsInRegion.add(ownerTown.getID());
        super.color = StringUtil.randomColor();
    }

    //////////////////////////////////////
    //          ITerritoryData          //
    //////////////////////////////////////

    @Override
    public String getOldID() {
        return id;
    }

    @Override
    public String getOldName() {
        return name;
    }

    public int getHierarchyRank() {
        return 1;
    }

    @Override
    public String getColoredName() {
        return "§b" + getName();
    }
    @Override
    public String getLeaderID(){
        if(leaderID == null)
            leaderID = getCapital().getLeaderID();
        return leaderID;
    }

    @Override
    public PlayerData getLeaderData(){
        return PlayerDataStorage.get(getLeaderID());
    }
    @Override
    public void setLeaderID(String newLeaderID){
        this.leaderID = newLeaderID;
    }

    @Override
    public boolean isLeader(String id) {
        return getLeaderID().equals(id);
    }

    @Override
    protected String getOldDescription() {
        return description;
    }


    @Override
    public ItemStack getOldIcon() {
        if(this.territoryIcon == null){
            if(this.regionIconType == null){
                return getCapital().getIcon();
            }
            else { // regionIconType is a legacy code, it should not be updated anymore but we keep it for compatibility (todo delete before v0.1)
                territoryIcon = new CustomIcon(new ItemStack(Material.getMaterial(regionIconType)));
            }
        }
        return territoryIcon.getIcon();
    }

    @Override
    public void setIcon(ItemStack icon) {
        this.territoryIcon = new CustomIcon(icon);
    }


    @Override
    public Collection<String> getPlayerIDList(){
        ArrayList<String> playerList = new ArrayList<>();
        for (TerritoryData townData : getSubjects()){
            playerList.addAll(townData.getPlayerIDList());
        }
        return playerList;
    }

    @Override
    public Collection<PlayerData> getPlayerDataList(){
        ArrayList<PlayerData> playerDataList = new ArrayList<>();
        for (String playerID : getPlayerIDList()){
            playerDataList.add(PlayerDataStorage.get(playerID));
        }
        return playerDataList;
    }

    @Override
    public ClaimedChunkSettings getChunkSettings(){
        return null;
    }


    @Override
    public ItemStack getIconWithName() {
        ItemStack icon = getIcon();

        ItemMeta meta = icon.getItemMeta();
        if(meta != null){
            meta.setDisplayName(ChatColor.AQUA + getName());
            icon.setItemMeta(meta);
        }
        return icon;

    }

    @Override
    public ItemStack getIconWithInformations() {
        ItemStack icon = getIcon();

        ItemMeta meta = icon.getItemMeta();
        if(meta != null){
            meta.setDisplayName(ChatColor.AQUA + getName());

            List<String> lore = new ArrayList<>();
            lore.add(Lang.GUI_REGION_INFO_DESC0.get(getDescription()));
            lore.add(Lang.GUI_REGION_INFO_DESC1.get(getCapital().getName()));
            lore.add(Lang.GUI_REGION_INFO_DESC2.get(getNumberOfTownsIn()));
            lore.add(Lang.GUI_REGION_INFO_DESC3.get(getTotalPlayerCount()));
            lore.add(Lang.GUI_REGION_INFO_DESC4.get(getBalance()));
            lore.add(Lang.GUI_REGION_INFO_DESC5.get(getNumberOfClaimedChunk()));
            meta.setLore(lore);
            icon.setItemMeta(meta);
        }
        return icon;
    }

    public int getTotalPlayerCount() {
        int count = 0;
        for (TerritoryData town : getSubjects()){
            count += town.getPlayerIDList().size();
        }
        return count;
    }


    public String getCapitalID() {
        return capitalID;
    }

    @Override
    public int getChildColorCode() {
        return 0;
    }

    @Override
    public boolean haveOverlord() {
        return nationID != null;
    }

    @Override
    public void claimChunk(Player player, Chunk chunk) {
        PlayerData playerData = PlayerDataStorage.get(player);
        TownData townData = TownDataStorage.get(player);
        RegionData regionData = townData.getSpecificOverlord();

        if(ClaimBlacklistStorage.cannotBeClaimed(chunk)){
            player.sendMessage(ChatUtils.getTANString() + Lang.CHUNK_IS_BLACKLISTED.get());
            return;
        }


        if(!regionData.doesPlayerHavePermission(playerData, RolePermission.CLAIM_CHUNK)){
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NOT_LEADER_OF_REGION.get());
            return;
        }
        int cost = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("CostOfRegionChunk",5);

        if(regionData.getBalance() < cost){
            player.sendMessage(ChatUtils.getTANString() + Lang.REGION_NOT_ENOUGH_MONEY_EXTENDED.get(cost - regionData.getBalance()));
            return;
        }

        ClaimedChunk2 currentClaimedChunk = NewClaimedChunkStorage.get(chunk);
        if(!currentClaimedChunk.canPlayerClaim(player, regionData)){
            return;
        }

        regionData.removeFromBalance(cost);
        NewClaimedChunkStorage.claimRegionChunk(chunk, regionData.getID());
        player.sendMessage(ChatUtils.getTANString() + Lang.CHUNK_CLAIMED_SUCCESS_REGION.get());
    }

    public boolean hasNation() {
        return nationID != null;
    }

    public TerritoryData getOverlord() {
        return null;
    }

    @Override
    protected String getOverlordPrivate() {
        return null;
    }
    @Override
    public Double getOldTax() {
        return taxRate;
    }

    public List<TerritoryData> getSubjects() {
        List<TerritoryData> towns = new ArrayList<>();
        for (String townID : townsInRegion) {
            towns.add(TerritoryUtil.getTerritory(townID));
        }
        return towns;
    }

    @Override
    public boolean isCapital() {
        if(!hasNation())
            return false;
        return getOverlord().isCapital();
    }

    public int getNumberOfTownsIn() {
        return townsInRegion.size();
    }


    @Override
    public void addVassalPrivate(TerritoryData vassal) {
        addSubject(vassal.getID());
    }

    public void addSubject(String townID) {
        townsInRegion.add(townID);
    }


    public boolean isCapital( TownData town) {
        return isCapital(town.getID());
    }
    public boolean isCapital( String townID) {
        return capitalID.equals(townID);
    }
    public void setCapital(TownData town) {
        setCapital(town.getID());
    }
    public void setCapital(String townID) {
        this.capitalID = townID;
    }


    public double getBalance() {
        return StringUtil.handleDigits(balance);
    }

    @Override
    public void removeOverlordPrivate() {
        // Kingdoms are not implemented yet
    }

    public int getIncomeTomorrow() {
        int income = 0;
        for (TerritoryData town  : getSubjects()) {
            if(town.getBalance() > taxRate) {
                income += taxRate;
            }
        }
        return income;
    }

    @Override
    public void addToTax(double i) {
        taxRate += i;
    }

    @Override
    protected void collectTaxes() {
        for(TerritoryData town : getVassals()){
            if(town == null) continue;
            double tax = getTax();
            if(town.getBalance() < tax){
                TownsAndNations.getPlugin().getDatabaseHandler().addTransactionHistory(new SubjectTaxHistory(this,town,-1));
            }
            else {
                town.removeFromBalance(tax);
                addToBalance(tax);
                TownsAndNations.getPlugin().getDatabaseHandler().addTransactionHistory(new SubjectTaxHistory(this,town,tax));
            }
        }
    }

    public boolean isTownInRegion(TownData townData) {
        return townsInRegion.contains(townData.getID());
    }

    public int setBasicColor(int colorHex) {
        int red = (colorHex >> 16) & 0xFF;
        int green = (colorHex >> 8) & 0xFF;
        int blue = colorHex & 0xFF;

        red = Math.max(0, red - 25);
        green = Math.max(0, green - 25);
        blue = Math.max(0, blue - 25);

        return (red << 16) | (green << 8) | blue;
    }

    public void removeVassal(String townID) {
        townsInRegion.remove(townID);
    }

    @Override
    public double getChunkUpkeepCost() {
        return ConfigUtil.getCustomConfig(ConfigTag.MAIN).getDouble("RegionChunkUpkeepCost",0);
    }

    public boolean isPlayerInRegion(PlayerData playerData) {
        for (TerritoryData town : getSubjects()){
            if(town.isPlayerIn(playerData))
                return true;
        }
        return false;
    }


    public Collection<RegionClaimedChunk> getClaims() {
        Collection<RegionClaimedChunk> res = new ArrayList<>();
        for(ClaimedChunk2 claimedChunk : NewClaimedChunkStorage.getClaimedChunksMap().values()){
            if(claimedChunk instanceof RegionClaimedChunk regionClaimedChunk && regionClaimedChunk.getOwnerID().equals(getID())){
                res.add(regionClaimedChunk);
            }

        }
        return res;

    }


    @Override
    public RelationData getOldRelations() {
        if(relations == null)
            relations = new RelationData();
        return relations;
    }


    @Override
    public void addToBalance(double balance) {
        this.balance += balance;
    }

    @Override
    public void removeFromBalance(double balance) {
        this.balance -= balance;
    }

    @Override
    public void broadCastMessage(String message) {
        for(TerritoryData townData : getSubjects())
            townData.broadCastMessage(message);
    }

    @Override
    public void broadCastMessageWithSound(String message, SoundEnum soundEnum, boolean addPrefix) {
        for(TerritoryData townData : getSubjects())
            townData.broadCastMessageWithSound(message, soundEnum, addPrefix);
    }

    @Override
    public void broadCastMessageWithSound(String message, SoundEnum soundEnum) {
        for(TerritoryData townData : getSubjects())
            townData.broadCastMessageWithSound(message, soundEnum);
    }

    @Override
    public boolean haveNoLeader() {
        return false; //Region always have a leader
    }


    @Override
    public boolean atWarWith(String territoryID) {
        for(PlannedAttack plannedAttack : getAttacksInvolved()) {
            if(plannedAttack.getMainDefender().getID().equals(territoryID))
                return true;
        }
        return false;
    }

    @Override
    public void delete(){
        super.delete();
        broadCastMessageWithSound(Lang.BROADCAST_PLAYER_REGION_DELETED.get(getLeaderData().getName(), getColoredName()), SoundEnum.BAD);
        TeamUtils.updateAllScoreboardColor();
        RegionDataStorage.deleteRegion(this);
    }

    @Override
    public void openMainMenu(Player player) {
        PlayerGUI.dispatchPlayerRegion(player);
    }

    @Override
    public boolean canHaveVassals() {
        return true;
    }

    @Override
    public boolean canHaveOverlord() {
        return true;
    }

    @Override
    public List<String> getVassalsID() {
        return townsInRegion;
    }

    @Override
    public boolean isVassal(String territoryID) {
        return townsInRegion.contains(territoryID);
    }

    @Override
    public boolean isCapitalOf(String territoryID) {
        if(!hasNation())
            return false;
        return getOverlord().getCapitalID().equals(territoryID);
    }

    @Override
    public Collection<TerritoryData> getPotentialVassals() {
        return new ArrayList<>(TownDataStorage.getTownMap().values());
    }


    @Override
    public RankData getRank(PlayerData playerData) {
        return getRank(playerData.getRegionRankID());
    }

    @Override
    public List<GuiItem> getOrderedMemberList(PlayerData playerData) {
        List<GuiItem> res = new ArrayList<>();
        for (String playerUUID: getOrderedPlayerIDList()) {
            OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
            PlayerData playerIterateData = PlayerDataStorage.get(playerUUID);
            ItemStack playerHead = HeadUtils.getPlayerHead(playerIterate,
                    Lang.GUI_TOWN_MEMBER_DESC1.get(playerIterateData.getRegionRank().getColoredName()));

            GuiItem playerButton = ItemBuilder.from(playerHead).asGuiItem(event -> event.setCancelled(true));
            res.add(playerButton);
        }
        return res;
    }

    @Override
    protected void specificSetPlayerRank(PlayerData playerStat, int rankID) {
        playerStat.setRegionRankID(rankID);
    }

    @Override
    protected void addSpecificTaxes(Budget budget) {
        budget.addProfitLine(new SubjectTaxLine(this));
    }

    protected long getOldDateTime(){
        return dateTimeCreated;
    }
}
