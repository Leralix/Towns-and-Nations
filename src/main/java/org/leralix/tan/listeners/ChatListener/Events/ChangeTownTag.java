package org.leralix.tan.listeners.ChatListener.Events;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.Lang.Lang;
import org.leralix.tan.listeners.ChatListener.ChatListenerEvent;
import org.leralix.tan.utils.ChatUtils;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import java.util.function.Consumer;

import static org.leralix.tan.listeners.ChatListener.PlayerChatListenerStorage.removePlayer;

public class ChangeTownTag extends ChatListenerEvent {

    private TownData townData;
    private Consumer<Player> guiCallback;
    public ChangeTownTag(TownData townData, Consumer<Player> guiCallback) {
        this.townData = townData;
        this.guiCallback = guiCallback;
    }

    @Override
    public void execute(Player player, String message) {

        int size = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("prefixSize");
        if(message.length() != size){
            player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_NOT_RIGHT_SIZE.get(size));
            return;
        }

        townData.setTownTag(message);
        player.sendMessage(ChatUtils.getTANString() + Lang.CHANGE_MESSAGE_SUCCESS.get());
        removePlayer(player);
        openGui(guiCallback, player);

    }
}
