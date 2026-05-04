package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.tan.data.building.Building;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.function.Consumer;

public class ChangeBuildingName extends ChatListenerEvent {
    private final Building propertyToRename;
    private final Consumer<Player> guiCallback;
    private final int maxSize;

    public ChangeBuildingName(Building building, Consumer<Player> guiCallback, int maxSize) {
        this.propertyToRename = building;
        this.guiCallback = guiCallback;
        this.maxSize = maxSize;
    }

    @Override
    public boolean execute(Player player, ITanPlayer playerData, String message) {

        if (message.length() > maxSize) {
            TanChatUtils.message(player, Lang.MESSAGE_TOO_LONG.get(playerData, Integer.toString(maxSize)));
            return false;
        }

        propertyToRename.setName(message);
        TanChatUtils.message(player, Lang.CHANGE_MESSAGE_SUCCESS.get(playerData));
        openGui(guiCallback, player);
        return true;
    }
}
