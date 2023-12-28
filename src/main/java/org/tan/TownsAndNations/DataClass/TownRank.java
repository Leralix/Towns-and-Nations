package org.tan.TownsAndNations.DataClass;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.enums.TownRolePermission;

import java.util.*;

public class TownRank {

    private String name;
    private int level;
    private String rankIconName;
    private final List<String> players;
    private int salary;
    private final Set<TownRolePermission> permissions = EnumSet.noneOf(TownRolePermission.class);

    public void swapPayingTaxes() {
        this.isPayingTaxes = !this.isPayingTaxes;
    }

    private boolean isPayingTaxes;

    public TownRank(String name){
        this.name = name;
        this.rankIconName = "DANDELION";
        this.level = 5;
        this.players = new ArrayList<>();

        this.isPayingTaxes = true;
        this.salary = 0;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String newName){
        this.name = newName;
    }
    public int getLevel(){return this.level;}
    public void incrementLevel(){
        this.level = (this.level % 5) + 1;
    }
    public String getRankIconName(){
        return this.rankIconName;
    }
    public void addPlayer(String playerUUID){
        this.players.add(playerUUID);
    }
    public void addPlayer(Player player){
        this.players.add(player.getUniqueId().toString());
    }
    public void removePlayer(String playerUUID){
        this.players.remove(playerUUID);
    }
    public void removePlayer(Player player){
        this.players.remove(player.getUniqueId().toString());
    }
    public List<String> showPlayers(){
        return this.players;
    }
    public boolean isPlayerIn(String playerUUID)
    {
        return this.players.contains(playerUUID);
    }
    public boolean isPlayerIn(Player player){
        return isPlayerIn(player.getUniqueId().toString());
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
    public void removeOneFromSalary() {
        this.salary -= 1;
    }

    public int getSalary() {
        return this.salary;
    }

}
