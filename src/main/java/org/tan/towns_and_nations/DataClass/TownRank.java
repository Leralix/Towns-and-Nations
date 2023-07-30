package org.tan.towns_and_nations.DataClass;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.enums.Permission;

import java.util.*;

public class TownRank {

    private String name;
    private int level;
    private String rankIconName;
    private List<String> players;
    private Map<Permission, Boolean> permissions;
    public void swapPayingTaxes() {
        this.isPayingTaxes = !this.isPayingTaxes;
    }

    private boolean isPayingTaxes;

    public TownRank(String name){
        this.name = name;
        this.rankIconName = "DANDELION";
        this.level = 5;
        this.players = new ArrayList<>();
        this.permissions = new EnumMap<>(Permission.class);
        // initialiser toutes les permissions à false par défaut
        for (Permission permission : Permission.values()) {
            this.permissions.put(permission, false);
        }
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
    public void grantPermission(Permission permission) {
        permissions.put(permission, true);
    }
    // Révoquer une permission
    public void revokePermission(Permission permission) {
        permissions.put(permission, false);
    }

    public boolean hasPermission(Permission permission) {
        return permissions.getOrDefault(permission, false);
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


}
