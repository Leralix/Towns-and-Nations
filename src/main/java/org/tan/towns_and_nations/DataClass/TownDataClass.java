package org.tan.towns_and_nations.DataClass;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;

import java.util.*;
import java.util.ArrayList;
import java.util.Date;

public class TownDataClass {

    private String TownId;
    private String TownName;
    private String UuidLeader;
    private final HashMap<String,TownRank> roles;
    private String townDefaultRank;
    private String Description;
    public boolean open;
    public String DateCreated;
    private String townIconMaterialCode;
    private final ArrayList<String> townPlayerListId = new ArrayList<String>();
    private TownTreasury townTreasury;
    private TownLevel townLevel;
    private ClaimedChunkSettings chunkSettings;
    private TownRelationClass relations;


    public TownDataClass( String townId, String townName, String uuidLeader){
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
        addTownRank(townDefaultRankName);
        setTownDefaultRank(townDefaultRankName);
        getRank(townDefaultRankName).addPlayer(uuidLeader);


        PlayerStatStorage.getStat(uuidLeader).setRank(this.townDefaultRank);


        this.relations = new TownRelationClass();
        this.chunkSettings = new ClaimedChunkSettings();

        this.townLevel = new TownLevel();
        this.townTreasury = new TownTreasury();
    }

    public void addTownRank(String rankName){
        this.roles.put(rankName,new TownRank(rankName));
    }
    public void addTownRank(String rankName, TownRank townRank){
        this.roles.put(rankName,townRank);
    }
    public void removeTownRank(String key){
        this.roles.remove(key);
    }


    public String getTownId() {
        return this.TownId;
    }
    public void setTownId(String townId) {
        this.TownId = townId;
    }
    public void setTownName(String townName) {
        this.TownName = townName;
    }

    public String getTownName(){
        return this.TownName;
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

    public ArrayList<String> getPlayerList(){
        return townPlayerListId;
    }

    public TownRelationClass getRelations(){
        return relations;
    }
    public void addTownRelations(String relation,String townId){
        this.relations.addRelation(relation,townId);
    }
    public void removeTownRelations(String relation, String townId) {
        this.relations.removeRelation(relation,townId);
    }
    public ClaimedChunkSettings getChunkSettings() {
        return chunkSettings;
    }
    public void setChunkSettings(ClaimedChunkSettings claimedChunkSettings) {
        this.chunkSettings = claimedChunkSettings;
    }
    public void setTreasury(TownTreasury townTreasury) {
        this.townTreasury = townTreasury;
    }
    public boolean getTownRelation(String relation, String checkTownId){
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
    public void playerChangeRank(String playerUuid, String newRank) {
        
        // Add the player to the new rank
        if (roles.containsKey(newRank)) {
            roles.get(newRank).addPlayer(playerUuid);
        } else {
            System.out.println("Error: The rank " + newRank + " does not exist.");
            return;
        }

        // Remove the player from all ranks
        for (TownRank rank : roles.values()) {
            rank.removePlayer(playerUuid);
        }

    }
    public TownRank getRank(String rankName){
        return this.roles.get(rankName);
    }
    public TownRank isRankExist(String rankName){
        return this.roles.get(rankName);
    }
    public HashMap<String,TownRank> getTownRanks(){
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


}
