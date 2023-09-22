package org.tan.TownsAndNations.DataClass;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;

import java.util.*;
import java.util.ArrayList;
import java.util.Date;

public class TownData {

    private String TownId;
    private String TownName;
    private String UuidLeader;
    private final HashMap<String,TownRank> roles;
    private String townDefaultRank;
    private String Description;
    public boolean open;
    public String DateCreated;
    private String townIconMaterialCode;
    private final HashSet<String> townPlayerListId = new HashSet<>();
    private final HashSet<String> PlayerJoinRequestSet = new HashSet<>();

    private final TownTreasury townTreasury;
    private final TownLevel townLevel;
    private ClaimedChunkSettings chunkSettings;
    private final TownRelations relations;


    public TownData(String townId, String townName, String uuidLeader){
        this.TownId = townId;
        this.UuidLeader = uuidLeader;
        this.TownName = townName;
        this.Description = "Description par défaut";
        this.open = false;
        this.DateCreated = new Date().toString();
        this.townIconMaterialCode = null;

        this.townPlayerListId.add(uuidLeader);
        this.roles = new HashMap<>();

        String townDefaultRankName = "default";
        addRank(townDefaultRankName);
        setTownDefaultRank(townDefaultRankName);
        getRank(townDefaultRankName).addPlayer(uuidLeader);


        PlayerDataStorage.get(uuidLeader).setRank(this.townDefaultRank);


        this.relations = new TownRelations();
        this.chunkSettings = new ClaimedChunkSettings();

        this.townLevel = new TownLevel();
        this.townTreasury = new TownTreasury();
    }

    public void addRank(String rankName){
        this.roles.put(rankName,new TownRank(rankName));
    }
    public void addRank(String rankName, TownRank townRank){
        this.roles.put(rankName,townRank);
    }
    public void removeRank(String key){
        this.roles.remove(key);
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

    public String getUuidLeader() {
        return this.UuidLeader;
    }
    public void setUuidLeader(String uuidLeader) {
        this.UuidLeader = uuidLeader;
    }

    public String getDescription() {
        return this.Description;
    }
    public void setDescription(String description) {
        this.Description = description;
    }

    public boolean isOpen() {
        return this.open;
    }
    public void setOpen(boolean openValue) {
        this.open = openValue;
    }

    public String getDateCreated() {
        return this.DateCreated;
    }
    public void setDateCreated(String dateCreated) {
        this.DateCreated = dateCreated;
    }

    public ItemStack getTownIconItemStack() {
        if(this.townIconMaterialCode == null){
            return null;
        }
        else
            return new ItemStack(Material.getMaterial(this.townIconMaterialCode));

    }
    public boolean isPlayerIn(Player player){
        String uuid = player.getUniqueId().toString();
        for (String townPlayerUUID : this.townPlayerListId) {
            if(uuid.equals(townPlayerUUID)){
                return true;
            }
        }
        return false;
    }

    public void setTownIconMaterialCode(Material material) {
        this.townIconMaterialCode = material.name();
    }
    public void addPlayer(String playerUUID){
        townPlayerListId.add(playerUUID);
        TownDataStorage.saveStats();
    }

    public void removePlayer(String playerUUID){
        townPlayerListId.remove(playerUUID);
        TownDataStorage.saveStats();
    }
    public void removePlayer(Player player){
        removePlayer(player.getUniqueId().toString());
    }

    public HashSet<String> getPlayerList(){
        return townPlayerListId;
    }

    public TownRelations getRelations(){
        return relations;
    }
    public void addTownRelations(org.tan.TownsAndNations.enums.TownRelation relation, String townId){
        this.relations.addRelation(relation,townId);
    }
    public void removeTownRelations(org.tan.TownsAndNations.enums.TownRelation relation, String townId) {
        this.relations.removeRelation(relation,townId);
    }
    public ClaimedChunkSettings getChunkSettings() {
        return chunkSettings;
    }
    public void setChunkSettings(ClaimedChunkSettings claimedChunkSettings) {
        this.chunkSettings = claimedChunkSettings;
    }
    public boolean getTownRelation(org.tan.TownsAndNations.enums.TownRelation relation, String checkTownId){
        for (String townId : getRelations().getOne(relation)){
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
        return this.townTreasury.getBalance();
    }
    public TownTreasury getTreasury(){
        return this.townTreasury;
    }

    public void broadCastMessage(String message){
        for (String playerId : townPlayerListId){
            Player player = Bukkit.getServer().getPlayer(UUID.fromString(playerId));
            if (player != null && player.isOnline()) {
                player.sendMessage(message);
            }
        }
    }

    public TownRank getRank(String rankName){
        return this.roles.get(rankName);
    }
    public TownRank isRankExist(String rankName){
        return this.roles.get(rankName);
    }
    public Map<String,TownRank> getTownRanks(){
        return this.roles;
    }
    public void createTownRank(String rankName){
        this.roles.put(rankName,new TownRank(rankName));
    }

    public void setTownDefaultRank(String newRank){
        this.townDefaultRank = newRank;
    }
    public String getTownDefaultRank(){
        return this.townDefaultRank;
    }

    public int getNumberOfRank(){
        return roles.size();
    }


    public TownRelation getRelationWith(TownData otherPlayerTown) {

        if(otherPlayerTown.getID().equals(this.getID()))
            return TownRelation.CITY;
        return this.relations.getRelationWith(otherPlayerTown);
    }
    public TownRelation getRelationWith(String otherPlayerTownID) {

        if(otherPlayerTownID.equals(this.getID()))
            return TownRelation.CITY;
        return this.relations.getRelationWith(otherPlayerTownID);
    }

    public boolean canAddMorePlayer(){
        return this.townPlayerListId.size() < this.townLevel.getPlayerCap();
    }
    public boolean canClaimMoreChunk(){
        return this.chunkSettings.getNumberOfClaimedChunk() < this.townLevel.getChunkCap();
    }

    public boolean isLeader(Player player){
        return this.UuidLeader.equals(player.getUniqueId().toString());
    }

    public void cancelAllRelation() {

        relations.cleanAll(getID());
    }


    public void addPlayerJoinRequest(String playerUUID) {
        PlayerJoinRequestSet.add(playerUUID);
    }
    public void addPlayerJoinRequest(Player player) {
        PlayerJoinRequestSet.add(player.getUniqueId().toString());
    }
    public void removePlayerJoinRequest(String playerUUID) {
        PlayerJoinRequestSet.remove(playerUUID);
    }
    public void removePlayerJoinRequest(Player player) {
        PlayerJoinRequestSet.remove(player.getUniqueId().toString());
    }
    public boolean isPlayerJoinRequest(String playerUUID) {
        return PlayerJoinRequestSet.contains(playerUUID);
    }
    public boolean isPlayerJoinRequest(Player player) {
        return PlayerJoinRequestSet.contains(player.getUniqueId().toString());
    }
    public HashSet<String> getPlayerJoinRequestSet(){
            return this.PlayerJoinRequestSet;
    }



}
