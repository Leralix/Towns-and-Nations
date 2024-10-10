package org.leralix.tan.dataclass;

import org.bukkit.entity.Player;
import org.leralix.tan.enums.TownRankEnum;
import org.leralix.tan.enums.TownRolePermission;

import java.util.*;

import static org.leralix.tan.enums.TownRankEnum.FIVE;

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
    public void setRankEnum(TownRankEnum rankEnum){
        this.rankEnum = rankEnum;
    }
    public int getLevel(){return this.rankEnum.getLevel();}
    public void incrementLevel(){
        this.rankEnum = rankEnum.nextRank();
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
        this.players.remove(playerUUID);
    }
    public void removePlayer(PlayerData player){
        removePlayer(player.getID());
    }
    public void removePlayer(Player player){
        removePlayer(player.getUniqueId().toString());
    }

    public List<String> getPlayers(){
        return this.players;
    }

    public boolean isPayingTaxes() {
        return this.isPayingTaxes;
    }

    public void setPayingTaxes(boolean payingTaxes) {
        this.isPayingTaxes = payingTaxes;
    }
    public void setRankIconName(String rankIconName) {
        this.rankIconName = rankIconName;
    }

    public int getNumberOfPlayer(){
        return players.size();
    }

    public void addPermission(TownRolePermission permission) {
        permissions.add(permission);
    }
    public boolean hasPermission(TownRolePermission permission) {
        return permissions.contains(permission);
    }
    public void removePermission(TownRolePermission permission) {
        permissions.remove(permission);
    }

    public void switchPermission(TownRolePermission permission) {
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
}
