package org.leralix.tan.dataclass.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.*;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TownClaimedChunk;
import org.leralix.tan.dataclass.newhistory.PlayerTaxHistory;
import org.leralix.tan.dataclass.territory.economy.*;
import org.leralix.tan.dataclass.territory.permission.ChunkPermission;
import org.leralix.tan.dataclass.wars.PlannedAttack;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.enums.ChunkPermissionType;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.enums.SoundEnum;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.ClaimBlacklistStorage;
import org.leralix.tan.storage.stored.*;
import org.leralix.tan.utils.*;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import java.util.*;

import static org.leralix.tan.enums.RolePermission.KICK_PLAYER;
import static org.leralix.tan.enums.SoundEnum.*;
import static org.leralix.tan.utils.ChatUtils.getTANString;
import static org.leralix.tan.utils.HeadUtils.getPlayerHead;

public class TownData extends TerritoryData {

    private final String TownId;
    private String TownName;
    private String UuidLeader;
    private Integer townDefaultRankID;
    private Long townDateTimeCreated;
    private String regionID;
    private boolean isRecruiting;
    private Double balance;
    private Double flatTax;
    private Integer chunkColor;
    private String townTag;
    private Level townLevel = new Level();
    private final HashSet<String> townPlayerListId = new HashSet<>();
    private Map<Integer, RankData> newRanks = new HashMap<>();
    private Collection<String> ownedLandmarks = new ArrayList<>();
    private HashSet<String> PlayerJoinRequestSet = new HashSet<>();
    private Map<String, PropertyData> propertyDataMap;
    private ClaimedChunkSettings chunkSettings = new ClaimedChunkSettings();
    private TeleportationPosition teleportationPosition;


    //First time creating a town
    public TownData(String townId, String townName, String leaderID){
        super(townId, townName, leaderID);
        this.TownId = townId;
        this.UuidLeader = leaderID;
        this.TownName = townName;
        this.townDateTimeCreated = new Date().getTime();
        this.isRecruiting = false;
        this.balance = 0.0;
        this.flatTax = 1.0;
        this.townDefaultRankID = 0;
        int prefixSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("prefixSize",3);
        this.townTag = townName.length() >= prefixSize ? townName.substring(0, prefixSize).toUpperCase() : townName.toUpperCase();
        super.color = StringUtil.randomColor();

        registerNewRank("default");
        if(leaderID != null)
            addPlayer(leaderID);
    }

    @Override //because old code was not using the centralised attribute
    protected Map<Integer, RankData> getRanks(){
        if(newRanks == null)
            newRanks = new HashMap<>();

        return newRanks;
    }

    @Override
    public RankData getRank(PlayerData playerData) {
        return getRank(playerData.getTownRankID());
    }

    public String getLeaderName() {
        if(this.UuidLeader == null)
            return Lang.NO_LEADER.get();
        return Bukkit.getOfflinePlayer(UUID.fromString(this.UuidLeader)).getName();
    }

    public Level getLevel() {
        return townLevel;
    }

    public void addPlayer(String playerDataID){
        PlayerData playerData = PlayerDataStorage.get(playerDataID);
        addPlayer(playerData);
    }

    public void addPlayer(PlayerData playerData){
        townPlayerListId.add(playerData.getID());
        getTownDefaultRank().addPlayer(playerData);
        playerData.joinTown(this);

        Player playerIterateOnline = playerData.getPlayer();
        if(playerIterateOnline != null)
            playerIterateOnline.sendMessage(getTANString() + Lang.TOWN_INVITATION_ACCEPTED_MEMBER_SIDE.get(getColoredName()));
        broadCastMessageWithSound(Lang.TOWN_INVITATION_ACCEPTED_TOWN_SIDE.get(playerData.getName()), MINOR_GOOD);

        for (TownData allTown : TownDataStorage.getTownMap().values()){
            allTown.removePlayerJoinRequest(playerData.getID());
        }

        TeamUtils.updateAllScoreboardColor();
        TownDataStorage.saveStats();
    }

    public void removePlayer(PlayerData playerData){
        getRank(playerData).removePlayer(playerData);
        townPlayerListId.remove(playerData.getID());
        playerData.leaveTown();
        TownDataStorage.saveStats();
    }

    @Override
    public Collection<String> getPlayerIDList(){
        return townPlayerListId;
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
    public ItemStack getIconWithName(){
        ItemStack itemStack = getIcon();

        ItemMeta meta = itemStack.getItemMeta();
        if(meta != null){
            meta.setDisplayName(ChatColor.GREEN + getName());
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    @Override
    public ItemStack getIconWithInformations(){
        ItemStack icon = getIcon();

        ItemMeta meta = icon.getItemMeta();
        if(meta != null){
            meta.setDisplayName(ChatColor.GREEN + getName());

            List<String> lore = new ArrayList<>();
            lore.add(Lang.GUI_TOWN_INFO_DESC0.get(getDescription()));
            lore.add(Lang.GUI_TOWN_INFO_DESC1.get(getLeaderName()));
            lore.add(Lang.GUI_TOWN_INFO_DESC2.get(getPlayerIDList().size()));
            lore.add(Lang.GUI_TOWN_INFO_DESC3.get(getNumberOfClaimedChunk()));
            lore.add(haveOverlord()? Lang.GUI_TOWN_INFO_DESC5_REGION.get(getOverlord().getName()): Lang.GUI_TOWN_INFO_DESC5_NO_REGION.get());

            meta.setLore(lore);
            icon.setItemMeta(meta);
        }
        return icon;
    }

    //////////////////////////////////////
    //          ITerritoryData          //
    //////////////////////////////////////

    @Override
    public String getOldID() {
        return this.TownId;
    }

    @Override
    public String getOldName(){
        return this.TownName;
    }

    @Override
    public int getHierarchyRank() {
        return 0;
    }

    @Override
    public String getColoredName() {
        return "§9" + getName();
    }

    @Override
    public String getLeaderID() {
        return this.UuidLeader;
    }

    @Override
    public PlayerData getLeaderData() {
        return PlayerDataStorage.get(this.UuidLeader);
    }

    @Override
    public void setLeaderID(String leaderID) {
        this.UuidLeader = leaderID;
    }


    @Override
    public boolean isLeader(String leaderID){
        return getLeaderID().equals(leaderID);
    }

    public boolean isLeader(@NotNull Player player){
        return isLeader(player.getUniqueId().toString());
    }




    //////////////////////////////////////
    //             IRelation            //
    //////////////////////////////////////

    @Override
    public ClaimedChunkSettings getChunkSettings() {
        if(chunkSettings == null)
            chunkSettings = new ClaimedChunkSettings();
        return chunkSettings;
    }

    //////////////////////////////////////
    //              IMoney              //
    //////////////////////////////////////

    @Override
    public double getBalance(){
        return StringUtil.handleDigits(balance);
    }



    @Override
    public void addToBalance(double balance){
        this.balance += balance;
    }
    @Override
    public void removeFromBalance(double balance){
        this.balance -= balance;
    }

    //////////////////////////////////////
    //            IBroadcast            //
    //////////////////////////////////////

    @Override
    public void broadCastMessage(String message){
        for (String playerId : townPlayerListId){
            Player player = Bukkit.getServer().getPlayer(UUID.fromString(playerId));
            if (player != null && player.isOnline()) {
                player.sendMessage(message);
            }
        }
    }
    @Override
    public void broadCastMessageWithSound(String message, SoundEnum soundEnum, boolean addPrefix){
        for (String playerId : townPlayerListId){
            Player player = Bukkit.getServer().getPlayer(UUID.fromString(playerId));
            if (player != null && player.isOnline()) {
                SoundUtil.playSound(player, soundEnum);
                if(addPrefix)
                    player.sendMessage(getTANString() + message);
                else
                    player.sendMessage(message);
            }
        }
    }

    @Override
    public void broadCastMessageWithSound(String message, SoundEnum soundEnum){
        broadCastMessageWithSound(message, soundEnum, true);
    }

    public RankData getTownDefaultRank(){
        return getRank(getDefaultRankID());
    }

    public int getNumberOfRank(){
        return newRanks.size();
    }

    public boolean isFull(){
        return this.townPlayerListId.size() >= this.townLevel.getPlayerCap();
    }
    public boolean canClaimMoreChunk(){
        return this.getNumberOfClaimedChunk() < this.townLevel.getChunkCap();
    }


    public void addPlayerJoinRequest(Player player) {
        addPlayerJoinRequest(player.getUniqueId().toString());
    }

    public void addPlayerJoinRequest(String playerUUID) {
        this.PlayerJoinRequestSet.add(playerUUID);
    }

    public void removePlayerJoinRequest(String playerUUID) {
        PlayerJoinRequestSet.remove(playerUUID);
    }
    public void removePlayerJoinRequest(Player player) {
        removePlayerJoinRequest(player.getUniqueId().toString());
    }
    public boolean isPlayerAlreadyRequested(String playerUUID) {
        return PlayerJoinRequestSet.contains(playerUUID);
    }

    public boolean isPlayerAlreadyRequested(Player player) {
        return isPlayerAlreadyRequested(player.getUniqueId().toString());
    }

    public Set<String> getPlayerJoinRequestSet(){
        return this.PlayerJoinRequestSet;
    }

    public boolean isRecruiting() {
        return isRecruiting;
    }

    public void swapRecruiting() {
        this.isRecruiting = !this.isRecruiting;
    }

    @Override
    public Double getOldTax() {
        if(this.flatTax == null)
            this.flatTax = 1.0;
        return this.flatTax;
    }

    @Override
    protected void collectTaxes() {

        for(PlayerData playerData : getPlayerDataList()){
            OfflinePlayer offlinePlayer = playerData.getOfflinePlayer();

            if (!playerData.getTownRank().isPayingTaxes()) continue;
            double tax = getTax();

            if(EconomyUtil.getBalance(offlinePlayer) > tax){
                EconomyUtil.removeFromBalance(offlinePlayer,tax);
                addToBalance(tax);
                TownsAndNations.getPlugin().getDatabaseHandler().addTransactionHistory(new PlayerTaxHistory(this,playerData,tax));
            }
            else{
                TownsAndNations.getPlugin().getDatabaseHandler().addTransactionHistory(new PlayerTaxHistory(this,playerData,-1));
            }
        }
    }



    @Override
    public double getChunkUpkeepCost() {
        return ConfigUtil.getCustomConfig(ConfigTag.MAIN).getDouble("TownChunkUpkeepCost",0);
    }

    public ChunkPermission getPermission(ChunkPermissionType type) {
        return this.chunkSettings.getPermission(type);
    }

    public void nextPermission(ChunkPermissionType type) {
        this.chunkSettings.nextPermission(type);
    }

    //////////////////////////////////////
    //           IChunkColor            //
    //////////////////////////////////////

    @Override
    public int getChildColorCode() {
        if(this.chunkColor == null)
            this.chunkColor = 0xff0000;
        return chunkColor;
    }


    public void setSpawn(Location location){
        this.teleportationPosition = new TeleportationPosition(location);
    }

    public boolean isSpawnSet(){
        return this.teleportationPosition != null;
    }

    public TeleportationPosition getSpawn(){
        return this.teleportationPosition;
    }

    public boolean teleportPlayerToSpawn(PlayerData playerData){
        return teleportPlayerToSpawn(playerData.getPlayer());
    }

    public boolean teleportPlayerToSpawn(Player player){
        if(isSpawnLocked()){
            return false;
        }
        if(this.teleportationPosition == null)
            return false;
        this.teleportationPosition.teleport(player);
        return true;
    }

    public boolean isSpawnLocked(){
        return this.townLevel.getBenefitsLevel("UNLOCK_TOWN_SPAWN") <= 0;
    }

    public boolean haveOverlord(){
        return this.regionID != null;
    }

    @Override
    public void claimChunk(Player player, Chunk chunk) {

        PlayerData playerData = PlayerDataStorage.get(player.getUniqueId().toString());


        if(ClaimBlacklistStorage.cannotBeClaimed(chunk)){
            player.sendMessage(ChatUtils.getTANString() + Lang.CHUNK_IS_BLACKLISTED.get());
            return;
        }

        if(!doesPlayerHavePermission(playerData, RolePermission.CLAIM_CHUNK)){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
            return;
        }

        if(!canClaimMoreChunk()){
            player.sendMessage(getTANString() + Lang.MAX_CHUNK_LIMIT_REACHED.get());
            return;
        }


        int cost = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("CostOfTownChunk",0);
        if(getBalance() < cost){
            player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY_EXTENDED.get(cost - getBalance()));
            return;
        }

        ClaimedChunk2 chunkData = NewClaimedChunkStorage.get(chunk);
        if(!chunkData.canPlayerClaim(player,this)){
            return;
        }

        if(getNumberOfClaimedChunk() != 0 &&
                !NewClaimedChunkStorage.isAdjacentChunkClaimedBySameTown(chunk,getID()) &&
                !ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("TownAllowNonAdjacentChunks",false)) {
            player.sendMessage(getTANString() + Lang.CHUNK_NOT_ADJACENT.get());
            return;
        }

        NewClaimedChunkStorage.unclaimChunk(chunk); //Un-claim in case it was already claimed and territory has the right to claim it
        NewClaimedChunkStorage.claimTownChunk(chunk,getID());

        player.sendMessage(getTANString() + Lang.CHUNK_CLAIMED_SUCCESS.get(
                getNumberOfClaimedChunk(),
                getLevel().getChunkCap())
        );
    }

    public RegionData getSpecificOverlord(){
        return RegionDataStorage.get(this.regionID);
    }

    @Override
    protected String getOverlordPrivate() {
        return regionID;
    }

    public String getRegionID(){
        return this.regionID;
    }

    public void setOverlordPrivate(String regionID){
        this.regionID = regionID;
    }

    @Override
    public Collection<TerritoryData> getPotentialVassals() {
        return Collections.emptyList();
    }

    public void removeOverlordPrivate() {
        this.regionID = null;
        for(PlayerData playerData : getPlayerDataList()){
            playerData.setRegionRankID(null);
        }
    }
    @Override
    public void addVassalPrivate(TerritoryData vassal) {
        //town have no vassals
    }

    @Override
    public void removeVassal(String townID) {
        //Town have no vassals
    }


    @Override
    public boolean isCapital() {
        if(!haveOverlord())
            return false;
        return getOverlord().getCapital().getID().equals(getID());
    }

    @Override
    public String getCapitalID() {
        return regionID;
    }


    public boolean isRegionalCapital() {
        if(!haveOverlord())
            return false;
        return getOverlord().getCapitalID().equals(getID());
    }

    public boolean haveRelationWith(TownData otherTown){
        return this.getRelationWith(otherTown) != null;
    }

    @Override
    public void setDefaultRank(int rankID){
        this.townDefaultRankID = rankID;
    }
    @Override
    public int getDefaultRankID() {
        return this.townDefaultRankID;
    }

    @Override
    public List<GuiItem> getOrderedMemberList(PlayerData playerData) {
        Player player = playerData.getPlayer();
        List<GuiItem> res = new ArrayList<>();
        for (String playerUUID: getOrderedPlayerIDList()) {
            OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
            PlayerData playerIterateData = PlayerDataStorage.get(playerUUID);
            ItemStack playerHead = HeadUtils.getPlayerHead(playerIterate,
                    Lang.GUI_TOWN_MEMBER_DESC1.get(playerIterateData.getTownRank().getColoredName()),
                    Lang.GUI_TOWN_MEMBER_DESC2.get(StringUtil.formatMoney(EconomyUtil.getBalance(playerIterate))),
                    doesPlayerHavePermission(playerData,KICK_PLAYER) ? Lang.GUI_TOWN_MEMBER_DESC3.get() : "");

            GuiItem playerButton = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                if(event.getClick() == ClickType.RIGHT){

                    PlayerData kickedPlayerData = PlayerDataStorage.get(playerIterate);
                    TownData townData = TownDataStorage.get(playerData);


                    if(!doesPlayerHavePermission(playerData,KICK_PLAYER)){
                        player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                        return;
                    }
                    if(townData.getRank(kickedPlayerData).isSuperiorTo(townData.getRank(playerData))){
                        player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION_RANK_DIFFERENCE.get());
                        return;
                    }
                    if(isLeader(kickedPlayerData)){
                        player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_CANT_KICK_LEADER.get());
                        return;
                    }
                    if(playerData.getID().equals(kickedPlayerData.getID())){
                        player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_CANT_KICK_YOURSELF.get());
                        return;
                    }

                    PlayerGUI.openConfirmMenu(player, Lang.CONFIRM_PLAYER_KICKED.get(playerIterate.getName()),
                            confirmAction -> {
                                kickPlayer(playerIterate);
                                PlayerGUI.openMemberList(player, this);
                            },
                            p -> PlayerGUI.openMemberList(player, this));
                }
            });
            res.add(playerButton);
        }
        return res;
    }

    @Override
    protected void specificSetPlayerRank(PlayerData playerStat, int rankID) {
        playerStat.setTownRankID(rankID);
    }

    @Override
    protected void addSpecificTaxes(Budget budget) {
        budget.addProfitLine(new PlayerTaxLine(this));
        if(haveOverlord())
            budget.addProfitLine(new OverlordTaxLine(this));
        budget.addProfitLine(new PropertyRentTax(this));
        budget.addProfitLine(new PropertySellTax(this));
    }

    public Map<String, PropertyData> getPropertyDataMap(){
        if(this.propertyDataMap == null)
            this.propertyDataMap = new HashMap<>();
        return this.propertyDataMap;
    }
    public Collection<PropertyData> getPropertyDataList(){
        return getPropertyDataMap().values();
    }

    public String nextPropertyID(){
        if(getPropertyDataMap().isEmpty())
            return "P0";
        int size = getPropertyDataMap().size();
        int lastID = Integer.parseInt(getPropertyDataMap().values().stream().toList().get(size - 1).getTotalID().split("P")[1]);
        return "P" + (lastID + 1);
    }

    public PropertyData registerNewProperty(Vector3D p1, Vector3D p2,PlayerData owner){
        String propertyID = nextPropertyID();
        String ID = this.getID() + "_" + propertyID;
        PropertyData newProperty = new PropertyData(ID,p1,p2,owner);
        this.propertyDataMap.put(propertyID, newProperty);
        owner.addProperty(newProperty);
        return newProperty;
    }

    public PropertyData getProperty(String ID){
        return getPropertyDataMap().get(ID);
    }

    public PropertyData getProperty(Location location) {
        for(PropertyData propertyData : getPropertyDataList()){
            if(propertyData.containsLocation(location)){
                return propertyData;
            }
        }
        return null;
    }

    public void removeProperty(PropertyData propertyData) {
        this.propertyDataMap.remove(propertyData.getPropertyID());
    }

    public String getTownTag() {
        if(this.townTag == null)
            setTownTag(this.TownName.substring(0,3).toUpperCase());
        return this.townTag;
    }
    public void setTownTag(String townTag) {
        this.townTag = townTag;
    }

    public String getColoredTag() {
        return getChunkColor() + "[" + getTownTag() + "]";
    }

    @SuppressWarnings("unused") //API
    public Collection<TownClaimedChunk> getClaims(){
        Collection<TownClaimedChunk> res = new ArrayList<>();
        for(ClaimedChunk2 claimedChunk : NewClaimedChunkStorage.getClaimedChunksMap().values()){
            if(claimedChunk instanceof TownClaimedChunk townClaimedChunk && townClaimedChunk.getOwnerID().equals(getID())){
                    res.add(townClaimedChunk);
                }

        }
        return res;
    }



    public void kickPlayer(OfflinePlayer kickedPlayer) {
        PlayerData kickedPlayerData = PlayerDataStorage.get(kickedPlayer);

        removePlayer(kickedPlayerData);
        broadCastMessageWithSound(Lang.GUI_TOWN_MEMBER_KICKED_SUCCESS.get(kickedPlayer.getName()), BAD);

        if(kickedPlayer.isOnline())
            kickedPlayer.getPlayer().sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_KICKED_SUCCESS_PLAYER.get());

    }



    public void upgradeTown(Player player) {
        PlayerData playerData = PlayerDataStorage.get(player);
        Level townLevel = this.getLevel();
        if(!doesPlayerHavePermission(playerData, RolePermission.UPGRADE_TOWN)){
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
            SoundUtil.playSound(player,NOT_ALLOWED);
            return;
        }
        if(this.getBalance() < townLevel.getMoneyRequiredForLevelUp()) {
            player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.get());
            SoundUtil.playSound(player,NOT_ALLOWED);
            return;
        }

        removeFromBalance(townLevel.getMoneyRequiredForLevelUp());
        townLevel.townLevelUp();
        SoundUtil.playSound(player,LEVEL_UP);
        player.sendMessage(getTANString() + Lang.BASIC_LEVEL_UP.get());
    }
    public void upgradeTown(Player player, TownUpgrade townUpgrade, int townUpgradeLevel){
        PlayerData playerData = PlayerDataStorage.get(player);

        if(!doesPlayerHavePermission(playerData, RolePermission.UPGRADE_TOWN)){
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
            SoundUtil.playSound(player,NOT_ALLOWED);
            return;
        }
        int cost = townUpgrade.getCost(townLevel.getUpgradeLevel(townUpgrade.getName()));
        if(this.getBalance() < cost ) {
            player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY_EXTENDED.get(cost - this.getBalance()));
            SoundUtil.playSound(player,NOT_ALLOWED);
            return;
        }
        Level townLevel = this.getLevel();
        if(townLevel.getUpgradeLevel(townUpgrade.getName()) >= townUpgrade.getMaxLevel()){
            player.sendMessage(getTANString() + Lang.TOWN_UPGRADE_MAX_LEVEL.get());
            SoundUtil.playSound(player,NOT_ALLOWED);
            return;
        }

        removeFromBalance(townUpgrade.getCost(townUpgradeLevel));
        townLevel.levelUp(townUpgrade);
        SoundUtil.playSound(player,LEVEL_UP);
        player.sendMessage(getTANString() + Lang.BASIC_LEVEL_UP.get());
    }

    public boolean haveNoLeader() {
        return this.UuidLeader == null;
    }

    public Collection<String> getOwnedLandmarks() {
        if(ownedLandmarks == null)
            ownedLandmarks = new ArrayList<>();
        return ownedLandmarks;
    }

    public int getNumberOfOwnedLandmarks() {
        return getOwnedLandmarks().size();
    }

    public void addLandmark(String landmarkID){
        getOwnedLandmarks().add(landmarkID);
    }
    public void addLandmark(Landmark landmark){
        addLandmark(landmark.getID());
        landmark.setOwnerID(this);
    }
    public void removeLandmark(String landmarkID){
        getOwnedLandmarks().remove(landmarkID);
    }
    public void removeLandmark(Landmark landmark){
        removeLandmark(landmark.getID());
        landmark.clearOwner();
    }

    public boolean ownLandmark(Landmark landmark) {
        return getOwnedLandmarks().contains(landmark.getID());
    }

    public boolean canClaimMoreLandmarks() {
        return getLevel().getTotalBenefits().get("MAX_LANDMARKS") > getNumberOfOwnedLandmarks();
    }


    @Override
    public boolean atWarWith(String territoryID) {
        for(PlannedAttack plannedAttack : getAttacksInvolved()) {
            if(plannedAttack.getMainDefender().getID().equals(territoryID))
                return true;
        }
        return false;
    }


    public void removeAllLandmark() {
        for(String landmarkID : getOwnedLandmarks()){
            Landmark landmark = LandmarkStorage.get(landmarkID);
            landmark.clearOwner();
        }
    }

    @Override
    public void delete(){
        super.delete();
        broadCastMessageWithSound(Lang.BROADCAST_PLAYER_TOWN_DELETED.get(getLeaderData().getName(), getColoredName()), BAD);
        removeAllLandmark(); //Remove all Landmark from the deleted town
        removeAllProperty(); //Remove all Property from the deleted town
        for(String playerID : getPlayerIDList()){ //Kick all Players from the deleted town
            removePlayer(PlayerDataStorage.get(playerID));
        }
       TeamUtils.updateAllScoreboardColor();
        TownDataStorage.deleteTown(this);
    }

    private void removeAllProperty() {
        for(PropertyData propertyData : getPropertyDataList()){
            propertyData.delete();
        }
    }

    @Override
    public boolean canConquerChunk(ClaimedChunk2 chunk) {
        return false;
    }

    @Override
    public void openMainMenu(Player player) {
        PlayerGUI.dispatchPlayerTown(player);
    }

    @Override
    public boolean canHaveVassals() {
        return false;
    }

    @Override
    public boolean canHaveOverlord() {
        return true;
    }

    @Override
    public List<String> getVassalsID() {
        return Collections.emptyList();
    }

    @Override
    public boolean isVassal(String territoryID) {
        return false;
    }

    @Override
    public boolean isCapitalOf(String territoryID) {
        if(!haveOverlord())
            return false;
        return getOverlord().getCapitalID().equals(getID());
    }

    protected long getOldDateTime(){
        return townDateTimeCreated;
    }
}

