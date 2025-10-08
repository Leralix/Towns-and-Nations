package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;

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
    public boolean execute(Player player, String message) {
        if(message.length() >= maxSize){
            player.sendMessage(Lang.MESSAGE_TOO_LONG.get(player, Integer.toString(maxSize)));
            return false;
        }
        landmark.setName(message);
        player.sendMessage(Lang.CHANGE_MESSAGE_SUCCESS.get(player));
        SoundUtil.playSound(player, SoundEnum.MINOR_LEVEL_UP);
        openGui(guiCallback,player);
        return true;
    }
}
