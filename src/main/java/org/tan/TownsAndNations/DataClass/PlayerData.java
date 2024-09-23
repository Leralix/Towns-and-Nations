package org.tan.TownsAndNations.DataClass;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.DataClass.territoryData.RegionData;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.DataClass.wars.CurrentAttacks;
import org.tan.TownsAndNations.enums.TownRolePermission;
import org.tan.TownsAndNations.storage.CurrentAttacksStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.storage.Invitation.TownInviteDataStorage;
import org.tan.TownsAndNations.utils.ConfigUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;


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
        this.Balance = ConfigUtil.getCustomConfig("config.yml").getInt("StartingMoney");
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
        TownData townData = TownDataStorage.get(this);
        return townData.getRank(this.townRankID).hasPermission(townData.getID(),rolePermission) ;
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

    public void notifyDeathToAttacks(){
        for(String attackID : getAttackInvolvedIn()){
            CurrentAttacks currentAttacks = CurrentAttacksStorage.get(attackID);
            if(currentAttacks != null){
                currentAttacks.playerKilled(this);
            }
            else {
                getAttackInvolvedIn().remove(attackID);
            }
        }
    }

    public void addWar(CurrentAttacks currentAttacks){
        if(getAttackInvolvedIn().contains(currentAttacks.getID())){
            return;
        }
        getAttackInvolvedIn().add(currentAttacks.getID());
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
        for(String attackID : getAttackInvolvedIn()){
            CurrentAttacks currentAttack = CurrentAttacksStorage.get(attackID);
            if(currentAttack == null){
                getAttackInvolvedIn().remove(attackID);
            }
            if(currentAttack.getDefenders().contains(territoryData)){
                return true;
            }
        }
        return false;
    }

    public void removeWar(CurrentAttacks currentAttacks){
        getAttackInvolvedIn().remove(currentAttacks.getID());
    }
}
