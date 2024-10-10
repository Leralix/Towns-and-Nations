package org.leralix.tan.dataclass;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.wars.CurrentAttacks;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.enums.TownRolePermission;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.storage.DataStorage.PlayerDataStorage;
import org.leralix.tan.storage.DataStorage.TownDataStorage;
import org.leralix.tan.storage.invitation.TownInviteDataStorage;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import java.util.*;


public class PlayerData {

    private final String UUID;
    private String PlayerName;
    private Integer Balance;
    private String TownId;
    private Integer townRankID;
    private List<String> propertiesListID;
    private List<String> attackInvolvedIn;

    public PlayerData(Player player) {
        this.UUID = player.getUniqueId().toString();
        this.PlayerName = player.getName();
        this.Balance = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("StartingMoney");
        this.TownId = null;
        this.townRankID = null;
        this.propertiesListID = new ArrayList<>();
        this.attackInvolvedIn = new ArrayList<>();
    }

    public String getID() {
        return UUID;
    }

    public String getName(){
        return PlayerName;
    }

    public void setName(String newPlayerName){
        this.PlayerName = newPlayerName;
    }

    public int getBalance() {
        return Balance;
    }

    public void setBalance(int balance) {
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

    public TownRank getTownRank() {
        return getTown().getRank(townRankID);
    }
    public void addToBalance(int money) {
        this.Balance = this.Balance + money;
    }
    public void removeFromBalance(int money) {
        this.Balance = this.Balance - money;
    }
    public boolean isTownLeader(){
        return this.UUID.equals(TownDataStorage.get(this).getLeaderID());
    }
    public boolean isRegionLeader(){
        if(!haveTown())
            return false;
        if(!getTown().haveOverlord())
            return false;
        return getRegion().isLeader(getID());
    }

    public boolean hasPermission(TownRolePermission rolePermission){
        if(isTownLeader())
            return true;
        if(!haveTown())
            return false;
        TownData townData = getTown();
        return townData.getRank(this.townRankID).hasPermission(rolePermission) ;
    }

    public void leaveTown(){
        this.TownId = null;
        this.townRankID = null;
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
        return getTown().getOverlord();
    }

    public UUID getUUID() {
        return java.util.UUID.fromString(this.UUID);
    }

    public void setTownRankID(int townRankID) {
        this.townRankID = townRankID;
    }
    public int getTownRankId(){
        return this.townRankID;
    }

    public void joinTown(TownData townData){
        this.TownId = townData.getID();
        this.townRankID = townData.getTownDefaultRankID();

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

    public Collection<String> getAttackInvolvedIn(){
        if(attackInvolvedIn == null)
            attackInvolvedIn = new ArrayList<>();
        return attackInvolvedIn;
    }

    public void notifyDeath(Player killer){
        Iterator<String> iterator = getAttackInvolvedIn().iterator();
        while (iterator.hasNext()) {
            String attackID = iterator.next();
            CurrentAttacks currentAttacks = CurrentAttacksStorage.get(attackID);
            if (currentAttacks != null) {
                currentAttacks.playerKilled(this, killer);
            } else {
                iterator.remove();
            }
        }
    }

    public void addWar(CurrentAttacks currentAttacks){
        if(getAttackInvolvedIn().contains(currentAttacks.getId())){
            return;
        }
        getAttackInvolvedIn().add(currentAttacks.getId());
    }

    public void updateCurrentAttack(){
        for(String attackID : getAttackInvolvedIn()){
            CurrentAttacks currentAttack = CurrentAttacksStorage.get(attackID);
            if(currentAttack == null){
                getAttackInvolvedIn().remove(attackID);
            }
            else if(!currentAttack.containsPlayer(this)){
                getAttackInvolvedIn().remove(attackID);
            }
            else{
                currentAttack.addPlayer(this);
            }
        }
    }

    public boolean isAtWarWith(ITerritoryData territoryData){
        if(territoryData == null){
            return false;
        }
        for(String attackID : getAttackInvolvedIn()){
            CurrentAttacks currentAttack = CurrentAttacksStorage.get(attackID);
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

    public void removeWar(@NotNull CurrentAttacks currentAttacks){
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
}
