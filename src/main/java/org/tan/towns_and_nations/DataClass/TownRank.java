package org.tan.towns_and_nations.DataClass;

import org.bukkit.entity.Player;
import org.tan.towns_and_nations.enums.Permission;

import java.util.*;

public class TownRank {

    private String name;
    private List<String> players;
    private Map<Permission, Boolean> permissions;


    public TownRank(String name){
        this.name = name;
        this.players = new ArrayList<>();
        this.permissions = new EnumMap<>(Permission.class);

        // initialiser toutes les permissions à false par défaut
        for (Permission permission : Permission.values()) {
            this.permissions.put(permission, false);
        }
    }

    public void grantPermission(Permission permission) {
        permissions.put(permission, true);
    }

    // Révoquer une permission
    public void revokePermission(Permission permission) {
        permissions.put(permission, false);
    }

    // Vérifier si le rôle a une permission
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



}
