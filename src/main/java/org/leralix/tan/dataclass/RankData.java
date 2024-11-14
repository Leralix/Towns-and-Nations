package org.leralix.tan.dataclass;

import org.bukkit.Material;
import org.leralix.tan.enums.TownRankEnum;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.storage.stored.PlayerDataStorage;

import java.util.*;

import static org.leralix.tan.enums.TownRankEnum.FIVE;

public class RankData {

    private Integer ID;
    private String name;
    private TownRankEnum rankEnum;
    private String rankIconName;
    private final List<String> players;
    private int salary;
    private final Set<RolePermission> permissions = EnumSet.noneOf(RolePermission.class);

    private boolean isPayingTaxes;

    public RankData(int id, String name){
        this.ID = id;
        this.name = name;
        this.rankEnum = FIVE;
        this.rankIconName = null;
        this.players = new ArrayList<>();
        this.isPayingTaxes = true;
        this.salary = 0;
    }

    public void swapPayingTaxes() {
        this.isPayingTaxes = !this.isPayingTaxes;
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
    public int getLevel(){return this.rankEnum.getLevel();}
    public void incrementLevel(){
        this.rankEnum = rankEnum.nextRank();
    }

    public void decrementLevel() {
        this.rankEnum = rankEnum.previousRank();
    }
    public Material getRankInconMaterial(){
        if(this.rankIconName == null)
            return rankEnum.getBasicRankIcon();
        return Material.getMaterial(this.rankIconName);
    }
    public void addPlayer(String playerUUID){
        this.players.add(playerUUID);
    }
    public void addPlayer(PlayerData playerData){
        addPlayer(playerData.getID());
    }
    public void removePlayer(String playerUUID){
        this.players.remove(playerUUID);
    }
    public void removePlayer(PlayerData player){
        removePlayer(player.getID());
    }

    public List<String> getPlayersID(){
        return this.players;
    }

    public List<PlayerData> getPlayers(){
        List<PlayerData> playerList = new ArrayList<>();
        for(String playerID : this.players){
            playerList.add(PlayerDataStorage.get(playerID));
        }
        return playerList;
    }

    public boolean isPayingTaxes() {
        return this.isPayingTaxes;
    }
    public void setRankIconName(String rankIconName) {
        this.rankIconName = rankIconName;
    }
    public int getNumberOfPlayer(){
        return players.size();
    }

    public void addPermission(RolePermission permission) {
        permissions.add(permission);
    }
    public boolean hasPermission(RolePermission permission) {
        return permissions.contains(permission);
    }
    public void removePermission(RolePermission permission) {
        permissions.remove(permission);
    }

    public void switchPermission(RolePermission permission) {
        if(hasPermission(permission))
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
    public void addFromSalary(int amount) {
        this.salary += amount;
    }
    public void removeFromSalary(int amount) {
        this.salary -= amount;
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

    public boolean isSuperiorTo(RankData rank) {
        return this.getRankEnum().getLevel() > rank.getRankEnum().getLevel();
    }


}
