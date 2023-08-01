package org.tan.towns_and_nations.DataClass;

import org.bukkit.entity.Player;
import org.tan.towns_and_nations.storage.TownDataStorage;

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

    public boolean checkAuth(TownDataClass Town){
        return Town.getRelations().getOne("alliance").contains(this.TownId);
    }

    public boolean isTownLeader(){
        return TownDataStorage.getTown(this).getUuidLeader().equals(this.UUID);
    }

}
