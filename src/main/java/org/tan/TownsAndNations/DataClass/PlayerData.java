package org.tan.TownsAndNations.DataClass;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.enums.TownRolePermission;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.storage.Invitation.TownInviteDataStorage;
import org.tan.TownsAndNations.storage.WarTaggedPlayer;
import org.tan.TownsAndNations.utils.ConfigUtil;

import java.util.UUID;


public class PlayerData {

    private final String UUID;
    private String PlayerName;
    private Integer Balance;
    private String TownId;
    private Integer townRankID;
    private String TownRank;

    public PlayerData(Player player) {
        this.UUID = player.getUniqueId().toString();
        this.PlayerName = player.getName();
        this.Balance = ConfigUtil.getCustomConfig("config.yml").getInt("StartingMoney");;
        this.TownId = null;
        this.TownRank = null;
    }

    public PlayerData(String UUID, String playerName, int balance, String townId, String townRank) {
        this.UUID = UUID;
        this.PlayerName = playerName;
        this.Balance = balance;
        this.TownId = townId;
        this.TownRank = townRank;
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
        if(Balance == null)
            Balance = 0;
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
    public void setTownId(String newTownId){
        this.TownId = newTownId;
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
        return TownDataStorage.get(this).getLeaderID().equals(this.UUID);
    }

    public boolean hasPermission(TownRolePermission rolePermission){
        if(isTownLeader())
            return true;
        TownData townData = TownDataStorage.get(this);
        return townData.getRank(this.townRankID).hasPermission(townData.getID(),rolePermission) ;
    }

    public void leaveTown(){
        this.TownId = null;
        this.TownRank = null;
        WarTaggedPlayer.removePlayer(this.getID());
    }

    public boolean haveRegion(){
        if(!this.haveTown()){
            return false;
        }
        return getTown().haveRegion();
    }
    public RegionData getRegion(){
        return getTown().getRegion();
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
    public String getOldRank(){
        return this.TownRank;
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
}
