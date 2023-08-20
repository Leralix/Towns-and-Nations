package org.tan.towns_and_nations.storage;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerChatListenerStorage {

    public static class PlayerChatData {
        private ChatCategory category;
        private String playerUUID;
        private Map<String, String> data;

        public PlayerChatData(ChatCategory category, String playerUUID, Map<String, String> data) {
            this.category = category;
            this.playerUUID = playerUUID;
            this.data = data;
        }

        public ChatCategory getCategory() {
            return category;
        }
        public String getPlayerUUID() {
            return playerUUID;
        }
        public Map<String, String> getData() {
            return data;
        }
    }

    public enum ChatCategory {
        CREATE_CITY, RANK_CREATION,RANK_RENAME, DONATION; // Vous pouvez renommer ces catégories comme vous le souhaitez

    }

    private static Map<String, PlayerChatData> ChatStorage = new HashMap<>();

    public static void addPlayer(ChatCategory category, Player p) {
        addPlayer(category, p, new HashMap<>());
    }

    public static void addPlayer(ChatCategory category, Player p, Map<String, String> data) {
        String playerId = p.getUniqueId().toString();

        if(ChatStorage.containsKey(playerId)){
            removePlayer(playerId);
        }
        PlayerChatData playerData = new PlayerChatData(category, playerId, data);
        ChatStorage.put(playerId, playerData);
    }

    public static void removePlayer(Player p) {
        String playerId = p.getUniqueId().toString();
        ChatStorage.remove(playerId);
    }
    public static void removePlayer(String playerUUID) {
        ChatStorage.remove(playerUUID);
    }

    public static Map<String, PlayerChatData> getAllData() {
        return ChatStorage;
    }

    public static PlayerChatData getPlayerData(String playerUUID) {
        return ChatStorage.get(playerUUID);
    }
}