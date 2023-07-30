package org.tan.towns_and_nations.storage;

import org.bukkit.entity.Player;

import java.util.*;

public class PlayerChatListenerStorage {

    /*
    First map: Category
    Second map: Player
    Third map: Player infos
     */
    private static Map<String,Map<String, Map<String,String>>> ChatListenerStorage = new HashMap<>();

    public static void addPlayer(String key,Player p){

        String playerId = p.getUniqueId().toString();

        //if Category doesn't exist, create it
        if(!ChatListenerStorage.containsKey(key)){
            ChatListenerStorage.put(key,new HashMap<String, Map<String,String>>());
        }
        if(!ChatListenerStorage.get(key).containsKey(playerId)){
            ChatListenerStorage.get(key).put(playerId,new HashMap<String,String>());
        }
    }

    public static void addPlayer(String key,Player p, HashMap<String, String> data){

        addPlayer(key,p);
        System.out.println(data);
        System.out.println(ChatListenerStorage);
        System.out.println(ChatListenerStorage.get(key));

        ChatListenerStorage.get(key).get(p.getUniqueId().toString()).putAll(data);
        System.out.println(ChatListenerStorage.get(key));

    }

    public static void removePlayer(String key,Player p){
        String playerId = p.getUniqueId().toString();
        ChatListenerStorage.get(key).remove(playerId);
    }
    public static boolean checkIfPlayerIn(String key,String uuid){
        System.out.println("la key: " + key);
        System.out.println(ChatListenerStorage.keySet());

        if(!ChatListenerStorage.containsKey(key)){
            return false;
        }

        return ChatListenerStorage.get(key).containsKey(uuid);
    }
    public static Map<String,Map<String, Map<String,String>>> getAllData(){
        return ChatListenerStorage;
    }
    public static Map<String, Map<String,String>> getData(String key){
        return ChatListenerStorage.get(key);
    }
    public static Map<String,String> getPlayerData(String key, String playerUUID){
        return ChatListenerStorage.get(key).get(playerUUID);
    }


}
