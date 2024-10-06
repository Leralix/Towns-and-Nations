package org.tan.TownsAndNations.listeners.ChatListener.Events;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.Landmark;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.listeners.ChatListener.ChatListenerEvent;
import org.tan.TownsAndNations.storage.DataStorage.LandmarkStorage;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.SoundUtil;
import org.tan.TownsAndNations.utils.config.ConfigTag;
import org.tan.TownsAndNations.utils.config.ConfigUtil;

import static org.tan.TownsAndNations.enums.MessageKey.LANDMARK_ID;
import static org.tan.TownsAndNations.enums.SoundEnum.MINOR_LEVEL_UP;
import static org.tan.TownsAndNations.listeners.ChatListener.PlayerChatListenerStorage.removePlayer;

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
