package org.leralix.tan.listeners.chat;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.leralix.tan.lang.Lang;

import java.util.HashMap;
import java.util.Map;

public class PlayerChatListenerStorage {

    private static final Map<Player, ChatListenerEvent> chatStorage = new HashMap<>();

    public static void register(Player player, ChatListenerEvent category) {
        chatStorage.put(player, category);
        player.sendMessage(TanChatUtils.getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));
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

    public static void playerMessage(Player player, @NotNull String message) {
        ChatListenerEvent event = chatStorage.get(player);
        if(event == null){
            return;
        }
        
        boolean success = event.execute(player, message);
        if(success){
            chatStorage.remove(player);
        }
        else {
            player.sendMessage(TanChatUtils.getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(Lang.CANCEL_WORD.get()));
        }
    }
}