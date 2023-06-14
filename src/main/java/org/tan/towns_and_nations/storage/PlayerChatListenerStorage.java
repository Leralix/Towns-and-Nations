package org.tan.towns_and_nations.storage;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerChatListenerStorage {


    private static Set<UUID> UuidDialogue = new HashSet<UUID>();


    public static void addPlayer(Player p){
        UuidDialogue.add(p.getUniqueId());
    }

    public static void removePlayer(Player p){
        UuidDialogue.remove(p.getUniqueId());
    }

    public static boolean checkIfPlayerIn(UUID uuid){
        return UuidDialogue.contains(uuid);
    }

    public static Set<UUID> getData(){
        return UuidDialogue;
    }

}
