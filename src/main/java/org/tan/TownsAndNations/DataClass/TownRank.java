package org.tan.TownsAndNations.DataClass;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.enums.TownRankEnum;
import org.tan.TownsAndNations.enums.TownRolePermission;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;

import java.util.*;

import static org.tan.TownsAndNations.TownsAndNations.isSQLEnabled;
import static org.tan.TownsAndNations.enums.TownRankEnum.FIVE;
import static org.tan.TownsAndNations.storage.DataStorage.TownDataStorage.getPlayerIdsByTownAndRank;

public class TownRank {

    private Integer ID;
    private String name;
    private TownRankEnum rankEnum;
    private String rankIconName;
    private final List<String> players;
    private int salary;
    private final Set<TownRolePermission> permissions = EnumSet.noneOf(TownRolePermission.class);

    private boolean isPayingTaxes;

    public TownRank(int id, String name){
        this.ID = id;
        this.name = name;
        this.rankEnum = FIVE;
        this.rankIconName = "DANDELION";
        this.players = new ArrayList<>();
        this.isPayingTaxes = true;
        this.salary = 0;
    }

    public TownRank(Integer id , String name,String rankEnum, String rankIconName, boolean isPayingTaxes,int salary){
        this.ID = id;
        this.name = name;
        this.rankEnum = TownRankEnum.valueOf(rankEnum);
        this.rankIconName = rankIconName;
        this.players = null;
        this.isPayingTaxes = isPayingTaxes;
        this.salary = salary;
    }

    public void swapPayingTaxes(String townID) {
        this.isPayingTaxes = !this.isPayingTaxes;
        if(isSQLEnabled())
            TownDataStorage.updateRank(townID,this);
    }
    public String getName(){
        return this.name;
    }
    public String getColoredName(){
        return this.rankEnum.getColor() + this.name;
    }
    public void setName(String newName){
        this.name = newName;
    }
    public TownRankEnum getRankEnum(){
        return this.rankEnum;
    }
    public void setRankEnum(String townID, TownRankEnum rankEnum){
        this.rankEnum = rankEnum;
        if(isSQLEnabled())
            TownDataStorage.updateRank(townID,this);
    }
    public int getLevel(){return this.rankEnum.getLevel();}
    public void incrementLevel(String townID){
        this.rankEnum = rankEnum.nextRank();
        if(isSQLEnabled())
            TownDataStorage.updateRank(townID,this);
    }
    public String getRankIconName(){
        return this.rankIconName;
    }
    public void addPlayer(String playerUUID){
        this.players.add(playerUUID);
    }
    public void addPlayer(Player player){
        addPlayer(player.getUniqueId().toString());
    }
    public void addPlayer(PlayerData playerData){
        addPlayer(playerData.getID());
    }
    public void removePlayer(String playerUUID){
        if(isSQLEnabled())
            return;
        this.players.remove(playerUUID);
    }
    public void removePlayer(PlayerData player){
        removePlayer(player.getID());
    }
    public void removePlayer(Player player){
        removePlayer(player.getUniqueId().toString());
    }

    public List<String> getPlayers(String townID){
        if(isSQLEnabled())
            return getPlayerIdsByTownAndRank(townID,getName());
        return this.players;
    }

    public boolean isPayingTaxes() {
        return this.isPayingTaxes;
    }

    public void setPayingTaxes(String townID, boolean payingTaxes) {
        this.isPayingTaxes = payingTaxes;
        if(isSQLEnabled())
            TownDataStorage.updateRank(townID,this);
    }
    public void setRankIconName(String townID, String rankIconName) {
        this.rankIconName = rankIconName;
        if(isSQLEnabled())
            TownDataStorage.updateRank(townID,this);
    }

    public int getNumberOfPlayer(String townID){
        if(isSQLEnabled())
            return TownDataStorage.getNumberOfPlayerByRank(townID,this.getName());
        else
            return players.size();
    }

    public void addPermission(TownRolePermission permission) {
        permissions.add(permission);
    }
    public boolean hasPermission(String townID,TownRolePermission permission) {
        if(isSQLEnabled())
            return TownDataStorage.getRankPermission(townID,getName(),permission);
        return permissions.contains(permission);
    }
    public void removePermission(TownRolePermission permission) {
        permissions.remove(permission);
    }

    public void switchPermission(String townID, TownRolePermission permission) {
        if(isSQLEnabled()){
            TownDataStorage.swapRankPermission(townID,this.getName(),permission);
            return;
        }
        if(hasPermission(townID,permission))
            removePermission(permission);
        else
            addPermission(permission);

    }


    public void setSalary(int salary) {
        this.salary = salary;
    }
    public void addOneFromSalary() {
        this.salary += 1;
    }
    public void addFromSalary(String townID, int amount) {
        this.salary += amount;
        if(isSQLEnabled())
            TownDataStorage.updateRank(townID,this);
    }
    public void removeFromSalary(String townID, int amount) {
        this.salary -= amount;
        if(isSQLEnabled())
            TownDataStorage.updateRank(townID,this);
    }

    public int getSalary() {
        return this.salary;
    }


    public Integer getID() {
        return ID;
    }
    public void setID(int id) {
        this.ID = id;
    }
}
