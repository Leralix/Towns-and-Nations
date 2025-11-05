package org.leralix.tan.listeners.chat;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.HashMap;
import java.util.Map;

public class PlayerChatListenerStorage {

    private static final Map<Player, ChatListenerEvent> chatStorage = new HashMap<>();

    public static void register(Player player, ChatListenerEvent category) {
        chatStorage.put(player, category);
        LangType langType = PlayerDataStorage.getInstance().getSync(player).getLang();
        TanChatUtils.message(player, Lang.WRITE_CANCEL_TO_CANCEL.get(langType, Lang.CANCEL_WORD.get(langType)));
        SoundUtil.playSound(player, SoundEnum.WRITE);
        player.closeInventory();
    }
    
    public static void removePlayer(Player p) {
        chatStorage.remove(p);
    }

    public static boolean contains(Player player){
        return chatStorage.containsKey(player);
    }

    public static void execute(Player player, @NotNull String message) {
        ChatListenerEvent event = chatStorage.get(player);
        if(event == null){
            chatStorage.remove(player);
            return;
        }
        
        boolean success = event.execute(player, message);
        if(success){
            chatStorage.remove(player);
        }
        else {
            LangType langType = PlayerDataStorage.getInstance().getSync(player).getLang();
            TanChatUtils.message(player, Lang.WRITE_CANCEL_TO_CANCEL.get(langType, Lang.CANCEL_WORD.get(langType)));
        }
    }
}