package org.tan.TownsAndNations.listeners.ChatListener;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.Lang.Lang;

import java.util.HashMap;
import java.util.Map;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class PlayerChatListenerStorage {

    private static final Map<Player, ChatListenerEvent> chatStorage = new HashMap<>();

    public static void register(Player player, ChatListenerEvent category) {
        chatStorage.put(player, category);
        player.sendMessage(getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));
        player.closeInventory();
    }

    public static ChatListenerEvent getPlayer(Player player){
        return chatStorage.get(player);
    }

    public static void removePlayer(Player p) {
        chatStorage.remove(p);
    }

    public static boolean contains(Player player){
        return chatStorage.containsKey(player);
    }
}