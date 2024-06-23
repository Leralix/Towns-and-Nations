package org.tan.TownsAndNations.DataClass;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.tan.TownsAndNations.DataClass.History.*;
import org.tan.TownsAndNations.DataClass.newChunkData.ClaimedChunk2;
import org.tan.TownsAndNations.DataClass.newChunkData.TownClaimedChunk;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.*;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.RegionDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.storage.PlayerChatListenerStorage;
import org.tan.TownsAndNations.utils.*;

import java.util.*;

import static org.tan.TownsAndNations.TownsAndNations.isSQLEnabled;
import static org.tan.TownsAndNations.enums.SoundEnum.*;
import static org.tan.TownsAndNations.enums.TownRolePermission.KICK_PLAYER;
import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;
import static org.tan.TownsAndNations.utils.EconomyUtil.removeFromBalance;
import static org.tan.TownsAndNations.utils.HeadUtils.getPlayerHead;

public class TownData {

    private String TownId;
    private String TownName;
    private String UuidLeader;
    private String townDefaultRank;
    private Integer townDefaultRankID;
    private String Description;
    public String DateCreated;
    private Long dateTimeCreated;
    private String townIconMaterialCode;
    private String regionID;
    private boolean isRecruiting;
    private Integer balance;
    private Integer flatTax;
    private Integer chunkColor;
    private String townTag;

    private ChunkHistory chunkHistory;
    private DonationHistory donationHistory;
    private MiscellaneousHistory miscellaneousHistory;
    private SalaryHistory salaryHistory;
    private TaxHistory taxHistory;

    private final HashSet<String> townPlayerListId = new HashSet<>();
    private final HashMap<String,TownRank> roles = new HashMap<>();
    private HashMap<Integer,TownRank> newRanks = new HashMap<>();
    private Collection<String> ownedLandmarks = new ArrayList<>();

    private HashSet<String> PlayerJoinRequestSet = new HashSet<>();
    private Map<String, PropertyData> propertyDataMap;

    private final TownLevel townLevel;
    private ClaimedChunkSettings chunkSettings;
    private final TownRelations relations;

    private TeleportationPosition teleportationPosition;

    //First time creating a town
    public TownData(String townId, String townName, String leaderID){
        this.TownId = townId;
        this.UuidLeader = leaderID;
        this.TownName = townName;
        this.Description = "default description";
        this.dateTimeCreated = new Date().getTime();
        this.townIconMaterialCode = null;
        this.isRecruiting = false;
        this.balance = 0;
        this.flatTax = 1;
        this.townDefaultRank = "default";
        this.townDefaultRankID = 0;
        this.townTag = townName.substring(0,3).toUpperCase();
        this.chunkColor = 0xff0000;

        this.chunkHistory = new ChunkHistory();
        this.donationHistory = new DonationHistory();
        this.miscellaneousHistory = new MiscellaneousHistory();
        this.salaryHistory = new SalaryHistory();
        this.taxHistory = new TaxHistory();

        this.relations = new TownRelations();
        this.chunkSettings = new ClaimedChunkSettings();
        this.townLevel = new TownLevel();


        addRank(townDefaultRank);
        if(leaderID != null)
            addPlayer(leaderID);
    }

    //used for sql, loading a town
    public TownData(String townId, String townName, String leaderID, String description, String dateCreated,
                    String townIconMaterialCode, String townDefaultRankName, long dateTimeCreated, Boolean isRecruiting, int balance,
                    int flatTax, int chunkColor, HashMap<Integer, TownRank> newRanks){
        this.TownId = townId;
        this.TownName = townName;
        this.UuidLeader = leaderID;
        this.Description = description;
        this.townIconMaterialCode = townIconMaterialCode;
        this.dateTimeCreated = dateTimeCreated;
        this.newRanks = newRanks;
        this.PlayerJoinRequestSet= new HashSet<>();
        this.townPlayerListId.add(leaderID);
        this.townDefaultRank = townDefaultRankName;
        this.isRecruiting = isRecruiting;
        this.balance = balance;
        this.flatTax = flatTax;
        this.chunkColor = chunkColor;

        this.relations = null;
        this.chunkSettings = null;
        this.townLevel = null;

    }

    public String getLeaderName() {
        if(this.UuidLeader == null)
            return Lang.NO_LEADER.get();
        return Bukkit.getOfflinePlayer(UUID.fromString(this.UuidLeader)).getName();
    }

    public String getID() {
        return this.TownId;
    }
    public void setID(String townId) {
        this.TownId = townId;
    }
    public String getName(){
        return this.TownName;
    }
    public void setName(String townName) {
        this.TownName = townName;
    }
    public TownRank addRank(String rankName){
        int nextRankId = 0;
        for(TownRank rank : this.getRanks()){
            if(rank.getID() >= nextRankId)
                nextRankId = rank.getID() + 1;
        }

        TownRank newRank = new TownRank(nextRankId, rankName);
        this.newRanks.put(nextRankId,newRank);
        return newRank;
    }
    public boolean isRankNameUsed(String message) {
        if(ConfigUtil.getCustomConfig("config.yml").getBoolean("AllowNameDuplication",false))
            return false;

        for (TownRank rank : this.getRanks()) {
            if (rank.getName().equals(message)) {
                return true;
            }
        }
        return false;
    }

    public void removeRank(int key){
        this.newRanks.remove(key);
    }
    public String getLeaderID() {
        return this.UuidLeader;
    }
    public PlayerData getLeaderData() {
        return PlayerDataStorage.get(this.UuidLeader);
    }
    public void setLeaderID(String leaderID) {
        this.UuidLeader = leaderID;
    }
    public String getDescription() {
        return this.Description;
    }
    public void setDescription(String description) {
        this.Description = description;
    }
    public long getDateTimeCreated() {
        if(this.dateTimeCreated == null)
            this.dateTimeCreated = new Date().getTime();
        return this.dateTimeCreated;
    }
    public ItemStack getTownIconItemStack() {
        if(haveNoLeader()){
            return new ItemStack(Material.SKELETON_SKULL);
        }
        if(this.townIconMaterialCode == null){
            return getPlayerHead(getName(), Bukkit.getOfflinePlayer(UUID.fromString(getLeaderID())));
        }
        Material material = Material.getMaterial(getTownIconMaterialCode());
        if(material == null)
            return null;
        else
            return new ItemStack(material);
    }
    public String getTownIconMaterialCode() {
        return townIconMaterialCode;
    }
    public void setTownIconMaterialCode(Material material) {
        this.townIconMaterialCode = material.name();
    }
    public void addPlayer(String playerDataID){
        PlayerData playerData = PlayerDataStorage.get(playerDataID);
        addPlayer(playerData);
    }
    public void addPlayer(PlayerData playerData){
        townPlayerListId.add(playerData.getID());
        getTownDefaultRank().addPlayer(playerData.getID());
        playerData.joinTown(this);
        TownDataStorage.saveStats();
    }

    public void removePlayer(PlayerData playerData){

        getRank(playerData).removePlayer(playerData);
        townPlayerListId.remove(playerData.getID());
        playerData.leaveTown();

        TownDataStorage.saveStats();
    }


    public HashSet<String> getPlayerList(){
        if(isSQLEnabled())
            return TownDataStorage.getPlayersInTown(TownId);
        else
            return townPlayerListId;
    }
    public TownRelations getRelations(){
        return relations;
    }
    public void addTownRelations(TownRelation relation, TownData townData){
        addTownRelations(relation,townData.getID());
    }
    public void addTownRelations(TownRelation relation, String otherTownID){
        if(isSQLEnabled())
            TownDataStorage.addTownRelation(this.getID(),otherTownID,relation);
        else
            this.relations.addRelation(relation,otherTownID);
    }
    public void removeTownRelations(TownRelation relation, TownData townData) {
        removeTownRelations(relation,townData.getID());
    }
    public void removeTownRelations(TownRelation relation, String townId) {
        if(isSQLEnabled())
            TownDataStorage.removeTownRelation(this.getID(),townId,relation);
        else
            this.relations.removeRelation(relation,townId);
    }

    public ClaimedChunkSettings getChunkSettings() {
        if(chunkSettings == null)
            chunkSettings = new ClaimedChunkSettings();
        return chunkSettings;
    }
    public void setChunkSettings(ClaimedChunkSettings claimedChunkSettings) {
        this.chunkSettings = claimedChunkSettings;
    }
    public boolean getTownRelationWithCurrent(TownRelation relation, String checkTownId){
        for (String townId : getTownWithRelation(relation)){
            if(townId.equals(checkTownId)){
                return true;
            }
        }
        return false;
    }
    public TownLevel getTownLevel() {
        return townLevel;
    }
    public int getBalance(){
        if (this.balance == null)
            this.balance = 0;
        return this.balance;
    }

    public void addToBalance(int balance){
        this.balance += balance;
    }

    public void removeToBalance(int balance){
        this.balance -= balance;
    }

    public void broadCastMessage(String message){
        for (String playerId : townPlayerListId){
            Player player = Bukkit.getServer().getPlayer(UUID.fromString(playerId));
            if (player != null && player.isOnline()) {
                player.sendMessage(getTANString() +  message);
            }
        }
    }

    public void broadCastMessageWithSound(String message, SoundEnum soundEnum){
        for (String playerId : townPlayerListId){
            Player player = Bukkit.getServer().getPlayer(UUID.fromString(playerId));
            if (player != null && player.isOnline()) {
                SoundUtil.playSound(player, soundEnum);
                player.sendMessage(getTANString() + message);
            }
        }
    }


    public TownRank getOldRank(String oldRankID){
        return this.roles.get(oldRankID);
    }
    public TownRank getRank(int rankID) {
        return this.newRanks.get(rankID);
    }

    public TownRank getRank(PlayerData playerData){
        return getRank(playerData.getTownRankId());
    }

    public TownRank getRank(Player player){
        return getRank(PlayerDataStorage.get(player));
    }

    public List<TownRank> getRanks(){
        if(newRanks == null)
            return null;
        return this.newRanks.values().stream().toList();
    }

    public void setTownDefaultRank(String newRank){
        this.townDefaultRank = newRank;
    }
    public String getTownDefaultRankName(){
        return this.townDefaultRank;
    }
    public TownRank getTownDefaultRank(){
        return getRank(getTownDefaultRankID());
    }

    public int getNumberOfRank(){
        return newRanks.size();
    }


    public TownRelation getRelationWith(TownData otherPlayerTown) {
        return getRelationWith(otherPlayerTown.getID());
    }
    public TownRelation getRelationWith(String otherTownID) {

        String townID = getID();

        if(townID.equals(otherTownID))
            return TownRelation.CITY;

        if(isSQLEnabled())
            return TownDataStorage.getRelationBetweenTowns(townID, otherTownID);
        else
            return this.relations.getRelationWith(otherTownID);
    }

    public boolean isFull(){
        return this.townPlayerListId.size() >= this.townLevel.getPlayerCap();
    }
    public boolean canClaimMoreChunk(){
        return this.getNumberOfClaimedChunk() < this.townLevel.getChunkCap();
    }

    public boolean isLeader(Player player){
        if(this.UuidLeader == null)
            return false;
        return this.UuidLeader.equals(player.getUniqueId().toString());
    }

    public void cancelAllRelation() {
        if(isSQLEnabled())
            TownDataStorage.removeAllTownRelationWith(getID());
        else
            this.relations.cleanAll(getID());
    }


    public void addPlayerJoinRequest(String playerUUID) {
        if(isSQLEnabled()){
            TownDataStorage.addPlayerJoinRequestToDB(playerUUID,this.getID());
        }
        else{
            this.PlayerJoinRequestSet.add(playerUUID);
        }
    }
    public void addPlayerJoinRequest(Player player) {
        addPlayerJoinRequest(player.getUniqueId().toString());
    }
    public void removePlayerJoinRequest(String playerUUID) {
        if(isSQLEnabled()){
            TownDataStorage.removePlayerJoinRequestFromDB(playerUUID,this.getID());
        }
        else{
            PlayerJoinRequestSet.remove(playerUUID);
        }

    }
    public void removePlayerJoinRequest(Player player) {
        removePlayerJoinRequest(player.getUniqueId().toString());
    }
    public boolean isPlayerAlreadyRequested(String playerUUID) {
        if(isSQLEnabled())
            return TownDataStorage.isPlayerAlreadyAppliedFromDB(playerUUID,this.getID());
        else
            return PlayerJoinRequestSet.contains(playerUUID);
    }
    public boolean isPlayerAlreadyRequested(Player player) {
        return isPlayerAlreadyRequested(player.getUniqueId().toString());
    }
    public HashSet<String> getPlayerJoinRequestSet(){
        if(isSQLEnabled())
            return TownDataStorage.getAllPlayerApplicationFrom(this.getID());
        else
            return this.PlayerJoinRequestSet;
    }

    //used to transition from 0.0.5 -> 0.0.6, will soon be deleted
    public void update(){
        if(this.PlayerJoinRequestSet == null)
            this.PlayerJoinRequestSet= new HashSet<>();
    }

    public boolean isRecruiting() {
        return isRecruiting;
    }

    public void setRecruiting(boolean isRecruiting) {
        this.isRecruiting = isRecruiting;
    }

    public void swapRecruiting() {
        this.isRecruiting = !this.isRecruiting;
    }

    public boolean isPlayerInTown(Player player){
        return this.townPlayerListId.contains(player.getUniqueId().toString());
    }

    public boolean isPlayerInTown(PlayerData player){
        return this.townPlayerListId.contains(player.getID());
    }

    public int getFlatTax() {
        if(this.flatTax == null)
            this.flatTax = 1;
        return this.flatTax;
    }

    public void addToFlatTax(int flatTax) {
        this.flatTax += flatTax;
    }

    public int getNumberOfClaimedChunk() {
        int count = 0;
        for (ClaimedChunk2 claimedChunk : NewClaimedChunkStorage.getClaimedChunksMap().values()) {
            if (claimedChunk.getOwnerID().equals(this.TownId)) {
                count++;
            }
        }
        return count;
    }

    public TownChunkPermission getPermission(ChunkPermissionType type) {
        if(isSQLEnabled())
            return TownDataStorage.getPermission(this.getID(),type);
        return this.chunkSettings.getPermission(type);
    }

    public void nextPermission(ChunkPermissionType type) {
        if(isSQLEnabled()){
            TownChunkPermission perm = TownDataStorage.getPermission(this.getID(),type);
            perm = perm.getNext();
            TownDataStorage.updateChunkPermission(this.getID(),type,perm);
        }
        else
            this.chunkSettings.nextPermission(type);
    }

    public ArrayList<String> getTownWithRelation(TownRelation relation){
        if(isSQLEnabled())
            return TownDataStorage.getTownsRelatedTo(getID(),relation);
        else
            return this.relations.getOne(relation);
    }

    public int getChunkColor() {
        if(this.chunkColor == null)
            this.chunkColor = 0xff0000;
        return chunkColor;
    }
    public String getChunkColorInHex() {
        if (this.chunkColor == null)
            this.chunkColor = 0xff0000;
        return String.format("#%06X", this.chunkColor);
    }

    public void setChunkColor(int color) {
        this.chunkColor = color;
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
        return teleportPlayerToSpawn(Bukkit.getPlayer(playerData.getUUID()));
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

    public boolean haveRegion(){
        return this.regionID != null;
    }

    public RegionData getRegion(){
        return RegionDataStorage.get(this.regionID);
    }
    public String getRegionID(){
        return this.regionID;
    }

    public void setRegion(RegionData region){
        setRegion(region.getID());
    }
    public void setRegion(String regionID){
        this.regionID = regionID;
    }


    public ChunkHistory getChunkHistory() {
        if(chunkHistory == null)
            chunkHistory = new ChunkHistory();
        return chunkHistory;
    }

    public DonationHistory getDonationHistory() {
        if(donationHistory == null)
            donationHistory = new DonationHistory();
        return donationHistory;
    }

    public MiscellaneousHistory getMiscellaneousHistory() {
        if(miscellaneousHistory == null)
            miscellaneousHistory = new MiscellaneousHistory();
        return miscellaneousHistory;
    }

    public SalaryHistory getSalaryHistory() {
        if(salaryHistory == null)
            salaryHistory = new SalaryHistory();
        return salaryHistory;
    }

    public TaxHistory getTaxHistory() {
        if(taxHistory == null)
            taxHistory = new TaxHistory();
        return taxHistory;
    }

    public boolean isLeaderOnline() {
        Player player = Bukkit.getServer().getPlayer(UUID.fromString(this.UuidLeader));
        return player != null && player.isOnline();
    }

    public void removeRegion() {
        this.regionID = null;
    }

    public boolean isRegionalCapital() {
        if(this.regionID == null)
            return false;
        return getRegion().getCapitalID().equals(getID());
    }

    public boolean haveRelationWith(TownData otherTown){
        return this.getRelationWith(otherTown) != null;
    }

    public void setPlayerRank(PlayerData playerStat, int rankID) {

        getRank(playerStat).removePlayer(playerStat);
        getRank(rankID).addPlayer(playerStat);
        playerStat.setTownRankID(rankID);
    }

    public Integer getTownDefaultRankID() {
        if(this.townDefaultRankID == null)
            this.townDefaultRankID = getRanks().get(0).getID(); //Bad fix of a bug
        return townDefaultRankID;
    }

    public void setTownDefaultRankID(int rankID){
        this.townDefaultRankID = rankID;
    }

    public Collection<TownRank> getOldRanks() {
        return roles.values();
    }
    public void setNewRanks(HashMap<Integer, TownRank> newRanks){
        if(this.newRanks == null)
            this.newRanks = new HashMap<>();
        this.newRanks.putAll(newRanks);
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

    public Collection<TownClaimedChunk> getClaims(){
        Collection<TownClaimedChunk> res = new ArrayList<>();
        for(ClaimedChunk2 claimedChunk : NewClaimedChunkStorage.getClaimedChunksMap().values()){
            if(claimedChunk instanceof TownClaimedChunk townClaimedChunk){
                if(townClaimedChunk.getOwnerID().equals(getID())){
                    res.add(townClaimedChunk);
                }
            }
        }
        return res;
    }

    public int computeNextTax() {

        int nextTaxes = 0;
        for (String playerID : getPlayerList()){
            PlayerData otherPlayerData = PlayerDataStorage.get(playerID);
            OfflinePlayer otherPlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerID));
            if(!otherPlayerData.getTownRank().isPayingTaxes()){
                continue;
            }
            if(EconomyUtil.getBalance(otherPlayer) < getFlatTax()){
                continue;
            }
            nextTaxes = nextTaxes + getFlatTax();
        }
        return nextTaxes;
    }

    public int getTotalSalaryCost() {
        int totalSalary = 0;
        for (TownRank rank : getRanks()) {

            List<String> playerIdList = rank.getPlayers(getID());
            totalSalary += playerIdList.size() * rank.getSalary();
        }
        return totalSalary;
    }

    public void addDonation(Player player, int amountDonated){
        int playerBalance = EconomyUtil.getBalance(player);

        if(playerBalance < amountDonated ){
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NOT_ENOUGH_MONEY.get());
            PlayerChatListenerStorage.removePlayer(player);
            return;
        }
        if(amountDonated <= 0 ){
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NEED_1_OR_ABOVE.get());
            PlayerChatListenerStorage.removePlayer(player);
            return;
        }

        TownData playerTown = TownDataStorage.get(player);

        removeFromBalance(player, amountDonated);

        playerTown.addToBalance(amountDonated);
        if(!isSQLEnabled())
            playerTown.getDonationHistory().add(player.getName(),player.getUniqueId().toString(),amountDonated);

        player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_SEND_MONEY_TO_TOWN.get(amountDonated));
        PlayerChatListenerStorage.removePlayer(player);
        SoundUtil.playSound(player, MINOR_LEVEL_UP);
    }

    public void kickPlayer(Player player, OfflinePlayer kickedPlayer) {
        PlayerData playerData = PlayerDataStorage.get(player);
        PlayerData kickedPlayerData = PlayerDataStorage.get(kickedPlayer);
        TownData townData = TownDataStorage.get(playerData);


        if(!playerData.hasPermission(KICK_PLAYER)){
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
            return;
        }
        int playerLevel = townData.getRank(playerData).getLevel();
        int kickedPlayerLevel = townData.getRank(kickedPlayerData).getLevel();
        if(playerLevel >= kickedPlayerLevel && !playerData.isTownLeader()){
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION_RANK_DIFFERENCE.get());
            return;
        }
        if(kickedPlayerData.isTownLeader()){
            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_CANT_KICK_LEADER.get());
            return;
        }
        if(playerData.getID().equals(kickedPlayerData.getID())){
            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_CANT_KICK_YOURSELF.get());
            return;
        }
        TownData town = TownDataStorage.get(playerData);
        town.removePlayer(kickedPlayerData);


        town.broadCastMessageWithSound(Lang.GUI_TOWN_MEMBER_KICKED_SUCCESS.get(kickedPlayer.getName()),
                BAD);
        if(kickedPlayer.isOnline())
            kickedPlayer.getPlayer().sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_KICKED_SUCCESS_PLAYER.get());

    }

    public void renameTown(Player player, int townCost, String newName) {
        PlayerChatListenerStorage.removePlayer(player);
        player.sendMessage(ChatUtils.getTANString() + Lang.CHANGE_MESSAGE_SUCCESS.get(this.getName(),newName));
        if(!isSQLEnabled())
            this.getMiscellaneousHistory().add(Lang.GUI_TOWN_SETTINGS_NEW_TOWN_NAME_HISTORY.get(this.getName() ,newName),townCost);
        this.removeToBalance(townCost);
        FileUtil.addLineToHistory(Lang.HISTORY_TOWN_NAME_CHANGED.get(player.getName(),this.getName(),newName));
        this.setName(newName);
    }

    public void upgradeTown(Player player) {
        PlayerData playerData = PlayerDataStorage.get(player);
        TownLevel townLevel = this.getTownLevel();
        if(!playerData.hasPermission(TownRolePermission.UPGRADE_TOWN)){
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
            SoundUtil.playSound(player,NOT_ALLOWED);
            return;
        }
        if(this.getBalance() < townLevel.getMoneyRequiredTownLevel()) {
            player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.get());
            SoundUtil.playSound(player,NOT_ALLOWED);
            return;
        }

        this.removeToBalance(townLevel.getMoneyRequiredTownLevel());
        townLevel.TownLevelUp();
        SoundUtil.playSound(player,LEVEL_UP);
        player.sendMessage(getTANString() + Lang.BASIC_LEVEL_UP.get());
    }
    public void upgradeTown(Player player, TownUpgrade townUpgrade, int townUpgradeLevel){
        PlayerData playerData = PlayerDataStorage.get(player);

        TownLevel townLevel = this.getTownLevel();
        if(!playerData.hasPermission(TownRolePermission.UPGRADE_TOWN)){
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
        if(townLevel.getUpgradeLevel(townUpgrade.getName()) >= townUpgrade.getMaxLevel()){
            player.sendMessage(getTANString() + Lang.TOWN_UPGRADE_MAX_LEVEL.get());
            SoundUtil.playSound(player,NOT_ALLOWED);
            return;
        }

        this.removeToBalance(townUpgrade.getCost(townUpgradeLevel));
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
    }

    public boolean ownLandmark(Landmark landmark) {
        return getOwnedLandmarks().contains(landmark.getID());
    }

    public boolean canClaimMoreLandmarks() {
        return getTownLevel().getTotalBenefits().get("MAX_LANDMARKS") > getNumberOfOwnedLandmarks();
    }
}
