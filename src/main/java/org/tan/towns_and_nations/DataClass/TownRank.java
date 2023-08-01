package org.tan.towns_and_nations.DataClass;

import org.bukkit.entity.Player;
import org.tan.towns_and_nations.enums.Permission;

import java.util.*;

public class TownRank {

    private String name;
    private int level;
    private String rankIconName;
    private final List<String> players;
    private final Set<Permission> permissions = EnumSet.noneOf(Permission.class);

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
    // Révoquer une permission
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

    public void addPermission(Permission permission) {
        permissions.add(permission);
    }
    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }
}
