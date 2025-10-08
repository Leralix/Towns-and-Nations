package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.utils.constants.Constants;

import java.util.function.Consumer;

public class ChangeTownTag extends ChatListenerEvent {

    private final TownData townData;
    private final Consumer<Player> guiCallback;

    public ChangeTownTag(TownData townData, Consumer<Player> guiCallback) {
        this.townData = townData;
        this.guiCallback = guiCallback;
    }

    @Override
    public boolean execute(Player player, String message) {

        int size = Constants.getPrefixSize();
        if(message.length() != size){
            player.sendMessage(Lang.MESSAGE_NOT_RIGHT_SIZE.get(player, Integer.toString(size)));
            return false;
        }

        townData.setTownTag(message);
        player.sendMessage(Lang.CHANGE_MESSAGE_SUCCESS.get(player));
        openGui(guiCallback, player);
        return true;
    }
}
