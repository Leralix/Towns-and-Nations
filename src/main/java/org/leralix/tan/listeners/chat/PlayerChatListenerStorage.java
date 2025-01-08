package org.leralix.tan.listeners.chat;

import org.bukkit.entity.Player;
import org.leralix.tan.enums.SoundEnum;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.SoundUtil;

import java.util.HashMap;
import java.util.Map;

import static org.leralix.tan.utils.ChatUtils.getTANString;

public class PlayerChatListenerStorage {

    private static final Map<Player, ChatListenerEvent> chatStorage = new HashMap<>();

    public static void register(Player player, ChatListenerEvent category) {
        chatStorage.put(player, category);
        player.sendMessage(getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));
        SoundUtil.playSound(player, SoundEnum.WRITE);
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