package org.tan.towns_and_nations.storage;

import org.bukkit.entity.Player;

import java.util.*;

public class PlayerChatListenerStorage {


    private static Map<String,ArrayList<UUID>> ChatListenerStorage = new HashMap<>();

    public static void load(){
        ChatListenerStorage.put("creationVille", new ArrayList<>());
        ChatListenerStorage.put("donation", new ArrayList<>());
        ChatListenerStorage.put("rank creation", new ArrayList<>());
    }
    public static void addPlayer(String key,Player p){

        if(!ChatListenerStorage.get(key).contains(p.getUniqueId()))
            ChatListenerStorage.get(key).add(p.getUniqueId());

    }
    public static void removePlayer(String key,Player p){
        ChatListenerStorage.get(key).remove(p.getUniqueId());
    }
    public static boolean checkIfPlayerIn(String key,UUID uuid){
        return ChatListenerStorage.get(key).contains(uuid);
    }
    public static ArrayList<UUID> getData(String key){
        return ChatListenerStorage.get(key);
    }

}
