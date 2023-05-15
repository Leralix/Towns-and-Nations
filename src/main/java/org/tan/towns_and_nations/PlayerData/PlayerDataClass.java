package org.tan.towns_and_nations.PlayerData;

import org.bukkit.entity.Player;

public class PlayerDataClass {

    String UUID;
    String PlayerName;
    int Balance;

    public PlayerDataClass(Player player) {
        this.UUID = player.getUniqueId().toString();
        this.PlayerName = player.getName();
        this.Balance = 0;
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

    public void addToBalance(int money) {
        this.Balance = this.Balance + money;
    }

    public void removeFromBalance(int money) {
        this.Balance = this.Balance - money;
    }

}
