package org.leralix.tan.dataclass;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.wars.CurrentAttack;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.storage.invitation.TownInviteDataStorage;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import java.util.*;


public class PlayerData {

    private final String UUID;
    private String storedName;
    private Double Balance;
    private String TownId;
    private Integer townRankID;
    private Integer regionRankID;
    private List<String> propertiesListID;
    private List<String> attackInvolvedIn;

    public PlayerData(Player player) {
        this.UUID = player.getUniqueId().toString();
        this.storedName = player.getName();
        this.Balance = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getDouble("StartingMoney");
        this.TownId = null;
        this.townRankID = null;
        this.regionRankID = null;
        this.propertiesListID = new ArrayList<>();
        this.attackInvolvedIn = new ArrayList<>();
    }

    public String getID() {
        return UUID;
    }

    public String getName(){
        if(storedName == null){
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(java.util.UUID.fromString(UUID));
            storedName = offlinePlayer.getName();
            if(storedName == null){
                storedName = "Unknown name";
            }
        }
        return storedName;
    }
    public void clearName(){
        this.storedName = null;
    }

    public double getBalance() {
        return this.Balance;
    }

    public void setBalance(double balance) {
        this.Balance = balance;
    }

    public String getTownId(){
        return this.TownId;
    }
    public TownData getTown(){
        return TownDataStorage.get(this);
    }
    public boolean haveTown(){
        return this.TownId != null;
    }

    public RankData getTownRank() {
        return getTown().getRank(getTownRankID());
    }
    public RankData getRegionRank() {
        return getRegion().getRank(getRegionRankID());
    }
    public void addToBalance(double amount) {
        this.Balance = this.Balance + amount;
    }
    public void removeFromBalance(double amount) {
        this.Balance = this.Balance - amount;
    }

    public void leaveTown(){
        this.TownId = null;
        this.townRankID = null;
        this.regionRankID = null;
    }

    public boolean haveRegion(){
        if(!this.haveTown()){
            return false;
        }
        return getTown().haveOverlord();
    }
    public RegionData getRegion(){
        if(!haveRegion())
            return null;
        return getTown().getRegion();
    }

    public UUID getUUID() {
        return java.util.UUID.fromString(UUID);
    }

    public void setTownRankID(int townRankID) {
        this.townRankID = townRankID;
    }
    public int getTownRankID(){
        return this.townRankID;
    }

    public void joinTown(TownData townData){
        this.TownId = townData.getID();
        this.townRankID = townData.getDefaultRankID();

        //remove all invitation
        for (TownData otherTown : TownDataStorage.getTownMap().values()) {
            if(otherTown == TownDataStorage.get(townData.getID())){
                continue;
            }
            TownInviteDataStorage.removeInvitation(UUID,otherTown.getID());
        }
    }

    public List<String> getPropertiesListID(){
        if(this.propertiesListID == null)
            this.propertiesListID = new ArrayList<>();
        return propertiesListID;
    }
    public void addProperty(PropertyData propertyData){
        getPropertiesListID().add(propertyData.getTotalID());
    }

    public List<PropertyData> getProperties(){
        List<PropertyData> propertyDataList = new ArrayList<>();

        for(String propertyID : getPropertiesListID()){
            String[] parts = propertyID.split("_");
            String tID = parts[0];
            String pID = parts[1];

            PropertyData nextProperty = TownDataStorage.get(tID).getProperty(pID);

            propertyDataList.add(nextProperty);
        }

        return propertyDataList;
    }

    public void removeProperty(PropertyData propertyData) {
        this.propertiesListID.remove(propertyData.getTotalID());
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(getUUID());
    }

    public List<String> getAttackInvolvedIn(){
        if(attackInvolvedIn == null)
            attackInvolvedIn = new ArrayList<>();
        return attackInvolvedIn;
    }

    public void notifyDeath(Player killer){
        Iterator<String> iterator = getAttackInvolvedIn().iterator();
        while (iterator.hasNext()) {
            String attackID = iterator.next();
            CurrentAttack currentAttacks = CurrentAttacksStorage.get(attackID);
            if (currentAttacks != null) {
                currentAttacks.playerKilled(this, killer);
            } else {
                iterator.remove();
            }
        }
    }

    public void addWar(CurrentAttack currentAttacks){
        if(getAttackInvolvedIn().contains(currentAttacks.getId())){
            return;
        }
        getAttackInvolvedIn().add(currentAttacks.getId());
    }

    public void updateCurrentAttack() {
        Iterator<String> iterator = getAttackInvolvedIn().iterator();
        while (iterator.hasNext()) {
            String attackID = iterator.next();
            CurrentAttack currentAttack = CurrentAttacksStorage.get(attackID);
            if (currentAttack == null || !currentAttack.containsPlayer(this)) {
                iterator.remove();
            } else {
                currentAttack.addPlayer(this);
            }
        }
    }

    public boolean isAtWarWith(TerritoryData territoryData){
        if(territoryData == null){
            return false;
        }
        for(String attackID : getAttackInvolvedIn()){
            CurrentAttack currentAttack = CurrentAttacksStorage.get(attackID);
            if(currentAttack == null){
                getAttackInvolvedIn().remove(attackID);
                continue;
            }
            if(currentAttack.getDefenders().contains(territoryData)){
                return true;
            }
        }
        return false;
    }

    public void removeWar(@NotNull CurrentAttack currentAttacks){
        getAttackInvolvedIn().remove(currentAttacks.getId());
    }

    public TownRelation getRelationWithPlayer(Player playerToAdd) {
        PlayerData otherPlayer = PlayerDataStorage.get(playerToAdd);
        if(!haveTown() || !otherPlayer.haveTown())
            return null;

        TownData playerTown = TownDataStorage.get(this);
        TownData otherPlayerTown = TownDataStorage.get(playerToAdd);

        TownRelation currentRelation = playerTown.getRelationWith(otherPlayerTown);

        //If no relation, check if maybe they are from the same region TODO implement this inside TownData#getRelationWith()
        if(currentRelation == null && playerTown.haveOverlord() && otherPlayerTown.haveOverlord()){
            currentRelation = playerTown.getOverlord().getRelationWith(otherPlayerTown.getOverlord());
        }

        return currentRelation;
    }

    public int getRegionRankID() {
        if(regionRankID == null)
            regionRankID = getRegion().getDefaultRankID();
        return regionRankID;
    }

    public void setRegionRankID(Integer rankID) {
        this.regionRankID = rankID;
    }

    public int getRankID(TerritoryData territoryData) {
        if(territoryData instanceof TownData){
            return getTownRankID();
        }
        else if(territoryData instanceof RegionData){
            return getRegionRankID();
        }
        return -1;
    }

    public List<TerritoryData> getAllTerritoriesPlayerIsIn() {
        List<TerritoryData> territories = new ArrayList<>();
        if(haveTown()){
            territories.add(getTown());
        }
        if(haveRegion()){
            territories.add(getRegion());
        }
        return territories;
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getServer().getOfflinePlayer(getUUID());
    }
}
