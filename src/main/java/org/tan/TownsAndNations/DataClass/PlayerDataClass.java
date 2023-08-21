package org.tan.TownsAndNations.DataClass;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.enums.TownRolePermission;
import org.tan.TownsAndNations.storage.TownDataStorage;

public class PlayerDataClass {

    private String UUID;
    private String PlayerName;
    private int Balance;
    private String TownId;
    private String TownRank;

    public PlayerDataClass(Player player) {
        this.UUID = player.getUniqueId().toString();
        this.PlayerName = player.getName();
        this.Balance = 0;
        this.TownId = null;
        this.TownRank = null;
    }


    public String getUuid() {
        return UUID;
    }

    public String getPlayerName(){
        return PlayerName;
    }

    public void setPlayerName(String newPlayerName){
        this.PlayerName = newPlayerName;
    }

    public int getBalance() {
        return Balance;
    }
    public void setRank(String rankName){
        this.TownRank = rankName;
    }
    public void setBalance(int balance) {
        this.Balance = balance;
    }

    public String getTownId(){
        return this.TownId;
    }
    public boolean haveTown(){
        return this.TownId != null;
    }
    public void setTownId(String newTownId){
        this.TownId = newTownId;
    }
    public String getTownRank() {
        return TownRank;
    }
    public void setTownRank(String townRank) {
        TownRank = townRank;
    }
    public void addToBalance(int money) {
        this.Balance = this.Balance + money;
    }
    public void removeFromBalance(int money) {
        this.Balance = this.Balance - money;
    }
    public boolean isTownLeader(){
        return TownDataStorage.getTown(this).getUuidLeader().equals(this.UUID);
    }
    public boolean hasPermission(TownRolePermission rolePermission){
        if(isTownLeader())
            return true;
        return TownDataStorage.getTown(this).getRank(this.TownRank).hasPermission(rolePermission) ;
    }
    public void leaveTown(){
        this.TownId = null;
        this.TownRank = null;
    }

}
