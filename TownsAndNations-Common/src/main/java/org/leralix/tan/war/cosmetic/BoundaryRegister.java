package org.leralix.tan.war.cosmetic;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class BoundaryRegister {

    private static final Set<Player> players = new HashSet<>();


    public static void switchPlayer(Player player){
        if(players.contains(player)){
            players.remove(player);
        }
        else {
            players.add(player);
        };
    }

    public static boolean isRegistered(Player player){
        return players.contains(player);
    }

}
