package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;

public class ChangeLandmarkName extends ChatListenerEvent {
    Landmark landmark;
    public ChangeLandmarkName(Landmark landmark) {
        this.landmark = landmark;
    }

    @Override
    public void execute(Player player, String message) {
        int nameMaxSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("landmarkNameMaxSize",25);
        if(message.length() >= nameMaxSize){
            player.sendMessage(TanChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(nameMaxSize));
            return;
        }
        PlayerChatListenerStorage.removePlayer(player);
        landmark.setName(message);
        player.sendMessage(TanChatUtils.getTANString() + Lang.CHANGE_MESSAGE_SUCCESS.get());
        SoundUtil.playSound(player, SoundEnum.MINOR_LEVEL_UP);
    }
}
