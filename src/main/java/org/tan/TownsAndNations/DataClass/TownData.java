package org.tan.TownsAndNations.DataClass;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.tan.TownsAndNations.DataClass.History.*;

import org.tan.TownsAndNations.DataClass.newChunkData.ClaimedChunk2;
import org.tan.TownsAndNations.enums.SoundEnum;
import org.tan.TownsAndNations.enums.TownChunkPermission;
import org.tan.TownsAndNations.enums.ChunkPermissionType;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.RegionDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.ConfigUtil;
import org.tan.TownsAndNations.utils.SoundUtil;

import java.util.*;
import java.util.Date;

import static org.tan.TownsAndNations.TownsAndNations.isSqlEnable;
import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class TownData {

    private String TownId;
    private String TownName;
    private String UuidLeader;
    private String townDefaultRank;
    private Integer townDefaultRankID;
    private String Description;
    public String DateCreated;
    private String townIconMaterialCode;
    private String regionID;
    private boolean isRecruiting;
    private Integer balance;
    private Integer flatTax;
    private Integer chunkColor;

    private ChunkHistory chunkHistory;
    private DonationHistory donationHistory;
    private MiscellaneousHistory miscellaneousHistory;
    private SalaryHistory salaryHistory;
    private TaxHistory taxHistory;

    private final HashSet<String> townPlayerListId = new HashSet<>();
    private final HashMap<String,TownRank> roles;
    private HashMap<Integer,TownRank> newRanks;

    private HashSet<String> PlayerJoinRequestSet;
    private Map<String, PropertyData> propertyDataMap;

    private final TownLevel townLevel;
    private ClaimedChunkSettings chunkSettings;
    private final TownRelations relations;

    private SpawnPosition spawnPosition;

    //First time creating a town
    public TownData(String townId, String townName, String leaderID){
        this.TownId = townId;
        this.UuidLeader = leaderID;
        this.TownName = townName;
        this.Description = "default description";
        this.DateCreated = new Date().toString();
        this.townIconMaterialCode = null;
        this.PlayerJoinRequestSet= new HashSet<>();
        this.townPlayerListId.add(leaderID);
        this.roles = new HashMap<>();
        this.newRanks = new HashMap<>();
        this.isRecruiting = false;
        this.balance = 0;
        this.flatTax = 1;
        this.townDefaultRank = "default";
        this.townDefaultRankID = 0;

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

    }

    //used for sql, loading a town
    public TownData(String townId, String townName, String leaderID, String description, String dateCreated,
                    String townIconMaterialCode, String townDefaultRankName, Boolean isRecruiting, int balance,
                    int flatTax, int chunkColor, HashMap<Integer, TownRank> newRanks){
        this.TownId = townId;
        this.TownName = townName;
        this.UuidLeader = leaderID;
        this.Description = description;
        this.DateCreated = dateCreated;
        this.townIconMaterialCode = townIconMaterialCode;
        this.newRanks = newRanks;
        this.PlayerJoinRequestSet= new HashSet<>();
        this.townPlayerListId.add(leaderID);
        this.roles = new HashMap<>();
        this.townDefaultRank = townDefaultRankName;
        this.isRecruiting = isRecruiting;
        this.balance = balance;
        this.flatTax = flatTax;
        this.chunkColor = chunkColor;

        this.relations = null;
        this.chunkSettings = null;
        this.townLevel = null;

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
        if(isSqlEnable())
            TownDataStorage.updateTownData(this);
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
        if(ConfigUtil.getCustomConfig("config.yml").getBoolean("AllowNameDuplication",true))
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
        if(isSqlEnable())
            TownDataStorage.updateTownData(this);
    }
    public String getDescription() {
        return this.Description;
    }
    public void setDescription(String description) {
        this.Description = description;
        if(isSqlEnable())
            TownDataStorage.updateTownData(this);
    }
    public String getDateCreated() {
        return this.DateCreated;
    }
    public void setDateCreated(String dateCreated) {
        this.DateCreated = dateCreated;
        if(isSqlEnable())
            TownDataStorage.updateTownData(this);
    }
    public ItemStack getTownIconItemStack() {
        if(this.townIconMaterialCode == null){
            return null;
        }
        else
            return new ItemStack(Material.getMaterial(this.townIconMaterialCode));
    }
    public String getTownIconMaterialCode() {
        return townIconMaterialCode;
    }
    public void setTownIconMaterialCode(Material material) {
        this.townIconMaterialCode = material.name();
        if(isSqlEnable())
            TownDataStorage.updateTownData(this);
    }
    public void addPlayer(PlayerData playerData){
        townPlayerListId.add(playerData.getID());
        getTownDefaultRank().addPlayer(playerData.getID());
        playerData.joinTown(this);

        TownDataStorage.saveStats();
    }

    public void removePlayer(PlayerData playerData){
        townPlayerListId.remove(playerData.getID());

        getRank(playerData).removePlayer(playerData);
        playerData.leaveTown();

        TownDataStorage.saveStats();
    }


    public HashSet<String> getPlayerList(){
        if(isSqlEnable())
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
        if(isSqlEnable())
            TownDataStorage.addTownRelation(this.getID(),otherTownID,relation);
        else
            this.relations.addRelation(relation,otherTownID);
    }
    public void removeTownRelations(TownRelation relation, TownData townData) {
        removeTownRelations(relation,townData.getID());
    }
    public void removeTownRelations(TownRelation relation, String townId) {
        if(isSqlEnable())
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
        if(isSqlEnable())
            return TownDataStorage.getTownUpgradeFromDatabase(TownId);
        else
            return townLevel;
    }
    public int getBalance(){
        if (this.balance == null)
            this.balance = 0;
        return this.balance;
    }

    public void addToBalance(int balance){
        this.balance += balance;
        if(isSqlEnable())
            TownDataStorage.updateTownData(this);
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
        if(isSqlEnable())
            TownDataStorage.updateTownData(this);
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

        if(isSqlEnable())
            return TownDataStorage.getRelationBetweenTowns(townID, otherTownID);
        else
            return this.relations.getRelationWith(otherTownID);
    }

    public boolean canAddMorePlayer(){
        if(isSqlEnable())
            return TownDataStorage.getPlayersInTown(getID()).size() < TownDataStorage.getTownUpgradeFromDatabase(getID()).getPlayerCap();
        return this.townPlayerListId.size() < this.townLevel.getPlayerCap();
    }
    public boolean canClaimMoreChunk(){
        if(isSqlEnable())
            return this.getNumberOfClaimedChunk() < TownDataStorage.getTownUpgradeFromDatabase(getID()).getChunkCap();
        return this.getNumberOfClaimedChunk() < this.townLevel.getChunkCap();
    }

    public boolean isLeader(Player player){
        return this.UuidLeader.equals(player.getUniqueId().toString());
    }

    public void cancelAllRelation() {
        if(isSqlEnable())
            TownDataStorage.removeAllTownRelationWith(getID());
        else
            this.relations.cleanAll(getID());
    }


    public void addPlayerJoinRequest(String playerUUID) {
        if(isSqlEnable()){
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
        if(isSqlEnable()){
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
        if(isSqlEnable())
            return TownDataStorage.isPlayerAlreadyAppliedFromDB(playerUUID,this.getID());
        else
            return PlayerJoinRequestSet.contains(playerUUID);
    }
    public boolean isPlayerAlreadyRequested(Player player) {
        return isPlayerAlreadyRequested(player.getUniqueId().toString());
    }
    public HashSet<String> getPlayerJoinRequestSet(){
        if(isSqlEnable())
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
        if(isSqlEnable())
            TownDataStorage.updateTownData(this);
    }

    public void swapRecruiting() {
        this.isRecruiting = !this.isRecruiting;
        if(isSqlEnable())
            TownDataStorage.updateTownData(this);
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
        if(isSqlEnable())
            TownDataStorage.updateTownData(this);
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
        if(isSqlEnable())
            return TownDataStorage.getPermission(this.getID(),type);
        return this.chunkSettings.getPermission(type);
    }

    public void nextPermission(ChunkPermissionType type) {
        if(isSqlEnable()){
            TownChunkPermission perm = TownDataStorage.getPermission(this.getID(),type);
            perm = perm.getNext();
            TownDataStorage.updateChunkPermission(this.getID(),type,perm);
        }
        else
            this.chunkSettings.nextPermission(type);
    }

    public ArrayList<String> getTownWithRelation(TownRelation relation){
        if(isSqlEnable())
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
        if(isSqlEnable())
            TownDataStorage.updateTownData(this);
    }

    public void setSpawn(Location location){
        this.spawnPosition = new SpawnPosition(location);
    }

    public boolean isSpawnSet(){
        return this.spawnPosition != null;
    }

    public void teleportPlayerToSpawn(PlayerData playerData){
        teleportPlayerToSpawn(Bukkit.getPlayer(playerData.getUUID()));
    }

    public boolean teleportPlayerToSpawn(Player player){
        if(isSpawnLocked()){
            player.sendMessage(getTANString() + "Vous ne pouvez pas vous téléporter au spawn de la ville, car la ville n'a pas encore été améliorée.");
            return false;
        }
        if(this.spawnPosition == null)
            return false;
        this.spawnPosition.teleportPlayerToSpawn(player);
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
        if(isSqlEnable())
            TownDataStorage.updateTownData(this);
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

    public boolean canInteractWithProperty(Block block, String playerID) {

        for(PropertyData propertyData : getPropertyDataList()){
            if(propertyData.containsBloc(block)){
                if(propertyData.getOwnerID().equals(playerID))
                    return true;
            }
        }
        return false;

    }
}
