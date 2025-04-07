package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;

import java.util.function.Consumer;

public class ChangeTownTag extends ChatListenerEvent {

    private final TownData townData;
    private final Consumer<Player> guiCallback;

    public ChangeTownTag(TownData townData, Consumer<Player> guiCallback) {
        this.townData = townData;
        this.guiCallback = guiCallback;
    }

    @Override
    public void execute(Player player, String message) {

        int size = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("prefixSize");
        if(message.length() != size){
            player.sendMessage(TanChatUtils.getTANString() + Lang.MESSAGE_NOT_RIGHT_SIZE.get(size));
            return;
        }

        townData.setTownTag(message);
        player.sendMessage(TanChatUtils.getTANString() + Lang.CHANGE_MESSAGE_SUCCESS.get());
        PlayerChatListenerStorage.removePlayer(player);
        openGui(guiCallback, player);

    }
}
