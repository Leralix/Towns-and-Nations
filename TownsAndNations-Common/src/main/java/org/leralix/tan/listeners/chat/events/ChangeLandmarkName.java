package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.data.building.landmark.Landmark;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.function.Consumer;

public class ChangeLandmarkName extends ChatListenerEvent {
    private final Landmark landmark;
    private final int maxSize;
    private final Consumer<Player> guiCallback;

    public ChangeLandmarkName(Landmark landmark, int maxSize, Consumer<Player> guiCallback) {
        this.landmark = landmark;
        this.maxSize = maxSize;
        this.guiCallback = guiCallback;
    }

    @Override
    public boolean execute(Player player, ITanPlayer playerData, String message) {
        if (message.length() >= maxSize) {
            TanChatUtils.message(player, Lang.MESSAGE_TOO_LONG.get(playerData, Integer.toString(maxSize)));
            return false;
        }
        landmark.setName(message);
        TanChatUtils.message(player, Lang.CHANGE_MESSAGE_SUCCESS.get(playerData), SoundEnum.MINOR_GOOD);
        openGui(guiCallback, player);
        return true;
    }
}
