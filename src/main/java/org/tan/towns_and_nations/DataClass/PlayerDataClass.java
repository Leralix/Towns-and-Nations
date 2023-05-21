package org.tan.towns_and_nations.DataClass;

import org.bukkit.entity.Player;

public class PlayerDataClass {

    private String UUID;
    private String PlayerName;
    private int Balance;
    private String TownId;

    public PlayerDataClass(Player player) {
        this.UUID = player.getUniqueId().toString();
        this.PlayerName = player.getName();
        this.Balance = 0;
        this.TownId = null;
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
    public void addToBalance(int money) {
        this.Balance = this.Balance + money;
    }

    public void removeFromBalance(int money) {
        this.Balance = this.Balance - money;
    }

}