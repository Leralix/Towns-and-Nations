package org.tan.TownsAndNations.DataClass;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.tan.TownsAndNations.enums.SoundEnum;
import org.tan.TownsAndNations.enums.TownChunkPermission;
import org.tan.TownsAndNations.enums.TownChunkPermissionType;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.storage.ClaimedChunkStorage;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;
import org.tan.TownsAndNations.utils.SoundUtil;

import java.util.*;
import java.util.Date;

import static org.tan.TownsAndNations.TownsAndNations.isSqlEnable;
import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class TownData {

    private String TownId;
    private String TownName;
    private String UuidLeader;
    private final HashMap<String,TownRank> roles;
    private String townDefaultRank;
    private String Description;
    public String DateCreated;
    private String townIconMaterialCode;
    private final HashSet<String> townPlayerListId = new HashSet<>();
    private boolean isRecruiting;
    private HashSet<String> PlayerJoinRequestSet;

    private Integer balance;
    private Integer flatTax;

    private Integer numberOfClaimedChunk;



    private final TownTreasury townTreasury;
    private final TownLevel townLevel;
    private ClaimedChunkSettings chunkSettings;
    private final TownRelations relations;

    //First time creating a town
    public TownData(String townId, String townName, String uuidLeader){
        this.TownId = townId;
        this.UuidLeader = uuidLeader;
        this.TownName = townName;
        this.Description = "Description par défaut";
        this.DateCreated = new Date().toString();
        this.townIconMaterialCode = null;
        this.PlayerJoinRequestSet= new HashSet<>();
        this.townPlayerListId.add(uuidLeader);
        this.roles = new HashMap<>();
        this.isRecruiting = false;
        this.balance = 0;
        this.flatTax = 1;
        this.numberOfClaimedChunk = 0;
        this.townDefaultRank = "default";

        addRank(townDefaultRank);
        getRank(townDefaultRank).addPlayer(uuidLeader);

        PlayerDataStorage.get(uuidLeader).setRank(this.townDefaultRank);


        this.relations = new TownRelations();
        this.chunkSettings = new ClaimedChunkSettings();

        this.townLevel = new TownLevel();
        this.townTreasury = new TownTreasury();
    }

    //used for sql, loading a town
    public TownData(String townId, String townName, String uuidLeader, String description,String dateCreated,
                    String townIconMaterialCode, String townDefaultRankName, Boolean isRecruiting, int balance,int flatTax){
        this.TownId = townId;
        this.TownName = townName;
        this.UuidLeader = uuidLeader;
        this.Description = description;
        this.DateCreated = dateCreated;
        this.townIconMaterialCode = townIconMaterialCode;
        this.PlayerJoinRequestSet= new HashSet<>();
        this.townPlayerListId.add(uuidLeader);
        this.roles = new HashMap<>();
        this.townDefaultRank = townDefaultRankName;
        this.isRecruiting = isRecruiting;
        this.balance = balance;
        this.flatTax = flatTax;

        this.relations = null;
        this.chunkSettings = null;
        this.townLevel = null;
        this.townTreasury = null;
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

    public void addRank(String rankName){
        TownRank newRank = new TownRank(rankName);
        if(isSqlEnable())
            TownDataStorage.createRole(getID(),newRank);
        else
            this.roles.put(rankName,newRank);
    }
    public void addRankForRename(String rankName, TownRank townRank){
        this.roles.put(rankName,townRank);
    }
    public void removeRank(String key){
        if(isSqlEnable())
            TownDataStorage.deleteRole(getID(),key);
        else
            this.roles.remove(key);
    }

    public String getUuidLeader() {
        return this.UuidLeader;
    }
    public void setUuidLeader(String uuidLeader) {
        this.UuidLeader = uuidLeader;
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
    public void addPlayer(String playerUUID){
        if(isSqlEnable())
            TownDataStorage.addPlayerToTownDatabase(TownId,playerUUID);
        else {
            townPlayerListId.add(playerUUID);
            TownDataStorage.saveStats();
        }
    }

    public void removePlayer(String playerUUID){
        if(isSqlEnable())
            TownDataStorage.removePlayerFromTownDatabase(playerUUID);
        else {
            townPlayerListId.remove(playerUUID);
            TownDataStorage.saveStats();
        }
    }
    public void removePlayer(Player player){
        removePlayer(player.getUniqueId().toString());
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
        Integer _bal = this.balance;

        //used to transition from 0.3.1 -> 0.4.0 when balance were stored in the treasury class
        if(_bal == null){
            this.balance = this.getTreasury().getBalance();
            return this.balance;
        }
        return _bal;
    }

    public void addToBalance(int balance){
        this.balance += balance;
        if(isSqlEnable())
            TownDataStorage.updateTownData(this);
    }

    public void removeToBalance(int balance){
        this.balance -= balance;
        if(isSqlEnable())
            TownDataStorage.updateTownData(this);
    }


    public TownTreasury getTreasury(){
        return this.townTreasury;
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

    public TownRank getRank(String rankName){
        if(isSqlEnable())
            return TownDataStorage.getRole(getID(), rankName);
        return this.roles.get(rankName);
    }
    public TownRank getRank(PlayerData playerData){
        return getRank(playerData.getTownRankID());
    }

    public TownRank getRank(Player player){
        return getRank(PlayerDataStorage.get(player).getTownRankID());
    }
    public List<TownRank> getTownRanks(){
        if(isSqlEnable())
            return TownDataStorage.getRanksByTownId(getID());
        return (List<TownRank>) this.roles.values();
    }

    public void setTownDefaultRank(String newRank){
        this.townDefaultRank = newRank;
        if(isSqlEnable())
            TownDataStorage.updateTownData(this);
    }
    public String getTownDefaultRank(){
        return this.townDefaultRank;
    }

    public int getNumberOfRank(){
        return roles.size();
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
            //TownDataStorage.removeTownRelation(this);
            return;
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

    public int getFlatTax() {
        Integer _tax = this.flatTax;
        if(_tax == null){ //used to transition from 0.3.1 -> 0.4.0 when balance were stored in the treasury class
            this.flatTax = this.getTreasury().getFlatTax();
            return this.flatTax;
        }
        return _tax;
    }

    public void addToFlatTax(int flatTax) {
        Integer _currentTax = this.flatTax;
        if(_currentTax == null) //used to transition from 0.3.1 -> 0.4.0 when balance were stored in the treasury class
            this.flatTax = this.getTreasury().getFlatTax() + flatTax;
        else{
            this.flatTax += flatTax;
            if(isSqlEnable())
                TownDataStorage.updateTownData(this);
        }
    }

    public int getNumberOfClaimedChunk() {
        if(isSqlEnable())
            return ClaimedChunkStorage.getNumberOfChunks(this.TownId);
        else{
            if(this.numberOfClaimedChunk == null) //used to transition from 0.3.1 -> 0.4.0
                this.numberOfClaimedChunk = this.getChunkSettings().getNumberOfClaimedChunk();
            return this.numberOfClaimedChunk;
        }
    }
    public void addNumberOfClaimChunk(int number) {
        if(isSqlEnable())
            return;

        if(this.numberOfClaimedChunk == null){ //used to transition from 0.3.1 -> 0.4.0
            this.numberOfClaimedChunk = this.getChunkSettings().getNumberOfClaimedChunk();
        }
        this.numberOfClaimedChunk += number;

    }

    public TownChunkPermission getPermission(TownChunkPermissionType type) {
        if(isSqlEnable())
            return TownDataStorage.getPermission(this.getID(),type);
        return this.chunkSettings.getPermission(type);
    }

    public void nextPermission(TownChunkPermissionType type) {
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
}
