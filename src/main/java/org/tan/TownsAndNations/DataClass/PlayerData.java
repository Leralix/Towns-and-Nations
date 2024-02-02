package org.tan.TownsAndNations.DataClass;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.enums.TownRolePermission;
import org.tan.TownsAndNations.storage.TownDataStorage;
import org.tan.TownsAndNations.utils.ConfigUtil;

import static org.tan.TownsAndNations.TownsAndNations.isSqlEnable;
import static org.tan.TownsAndNations.storage.PlayerDataStorage.updatePlayerDataInDatabase;

public class PlayerData {

    private final String UUID;
    private String PlayerName;
    private Integer Balance;
    private String TownId;
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


    public String getUuid() {
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
    public void setRank(String rankName){
        this.TownRank = rankName;
        if(isSqlEnable())
            updatePlayerDataInDatabase(this);
    }
    public void setBalance(int balance) {
        this.Balance = balance;
        if(isSqlEnable())
            updatePlayerDataInDatabase(this);
    }

    public String getTownId(){
        return this.TownId;
    }
    public boolean haveTown(){
        return this.TownId != null;
    }
    public void setTownId(String newTownId){
        this.TownId = newTownId;
        if(isSqlEnable())
            updatePlayerDataInDatabase(this);
    }
    public String getTownRankID() {
        return TownRank;
    }
    public TownRank getTownRank() {
        return TownDataStorage.get(this).getRank(this.TownRank);
    }
    public void addToBalance(int money) {
        this.Balance = this.Balance + money;
        if(isSqlEnable())
            updatePlayerDataInDatabase(this);
    }
    public void removeFromBalance(int money) {
        this.Balance = this.Balance - money;
        if(isSqlEnable())
            updatePlayerDataInDatabase(this);
    }
    public boolean isTownLeader(){
        return TownDataStorage.get(this).getUuidLeader().equals(this.UUID);
    }
    public boolean hasPermission(TownRolePermission rolePermission){
        if(isTownLeader())
            return true;
        TownData townData = TownDataStorage.get(this);
        return townData.getRank(this.TownRank).hasPermission(townData.getID(),rolePermission) ;
    }
    public void leaveTown(){
        this.TownId = null;
        this.TownRank = null;
        if(isSqlEnable())
            updatePlayerDataInDatabase(this);
    }

}
