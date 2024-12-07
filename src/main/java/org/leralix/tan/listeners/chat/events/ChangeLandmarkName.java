package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.utils.ChatUtils;
import org.leralix.tan.utils.SoundUtil;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import static org.leralix.tan.enums.SoundEnum.MINOR_LEVEL_UP;
import static org.leralix.tan.listeners.chat.PlayerChatListenerStorage.removePlayer;

public class ChangeLandmarkName extends ChatListenerEvent {
    Landmark landmark;
    public ChangeLandmarkName(Landmark landmark) {
        this.landmark = landmark;
    }

    @Override
    public void execute(Player player, String message) {
        int nameMaxSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("landmarkNameMaxSize",25);
        if(message.length() >= nameMaxSize){
            player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(nameMaxSize));
            return;
        }
        removePlayer(player);
        landmark.setName(message);
        player.sendMessage(ChatUtils.getTANString() + Lang.CHANGE_MESSAGE_SUCCESS.get());
        SoundUtil.playSound(player, MINOR_LEVEL_UP);
    }
}
