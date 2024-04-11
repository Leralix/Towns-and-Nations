package org.tan.TownsAndNations.storage;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.enums.ChatCategory;
import org.tan.TownsAndNations.enums.MessageKey;

import java.util.HashMap;
import java.util.Map;

public class PlayerChatListenerStorage {

    public static class PlayerChatData {
        private final ChatCategory category;
        private final String playerUUID;
        private final Map<MessageKey, String> data;

        public PlayerChatData(ChatCategory category, String playerUUID, Map<MessageKey, String> data) {
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
        public Map<MessageKey, String> getData() {
            return data;
        }
    }


    private static final Map<String, PlayerChatData> chatStorage = new HashMap<>();

    public static void addPlayer(ChatCategory category, Player p) {
        addPlayer(category, p, new HashMap<>());
    }


    public static void addPlayer(ChatCategory category, Player p, Map<MessageKey, String> data) {
        String playerId = p.getUniqueId().toString();

        if(chatStorage.containsKey(playerId)){
            removePlayer(playerId);
        }
        PlayerChatData playerData = new PlayerChatData(category, playerId, data);
        chatStorage.put(playerId, playerData);
    }

    public static void removePlayer(Player p) {
        String playerId = p.getUniqueId().toString();
        chatStorage.remove(playerId);
    }
    public static void removePlayer(String playerUUID) {
        chatStorage.remove(playerUUID);
    }

    public static Map<String, PlayerChatData> getAllData() {
        return chatStorage;
    }

    public static PlayerChatData getPlayerData(String playerUUID) {
        return chatStorage.get(playerUUID);
    }
    public static boolean contains(String playerUUID){
        return chatStorage.containsKey(playerUUID);
    }
}