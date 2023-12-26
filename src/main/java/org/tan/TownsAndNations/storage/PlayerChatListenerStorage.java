package org.tan.TownsAndNations.storage;

import org.bukkit.entity.Player;
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

    public enum ChatCategory {
        CREATE_CITY, RANK_CREATION,RANK_RENAME, DONATION, CHANGE_TOWN_NAME, CHANGE_DESCRIPTION

    }

    private static final Map<String, PlayerChatData> ChatStorage = new HashMap<>();

    public static void addPlayer(ChatCategory category, Player p) {
        addPlayer(category, p, new HashMap<>());
    }


    public static void addPlayer(ChatCategory category, Player p, Map<MessageKey, String> data) {
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