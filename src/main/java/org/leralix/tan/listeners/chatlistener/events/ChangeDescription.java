package org.leralix.tan.listeners.chatlistener.events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chatlistener.ChatListenerEvent;
import org.leralix.tan.utils.ChatUtils;
import org.leralix.tan.utils.FileUtil;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import java.util.function.Consumer;

import static org.leralix.tan.listeners.chatlistener.PlayerChatListenerStorage.removePlayer;

public class ChangeDescription extends ChatListenerEvent {

    private final TerritoryData territoryData;
    Consumer<Player> guiCallback;

    public ChangeDescription(@NotNull TerritoryData territoryData, Consumer<Player> guiCallback) {
        this.territoryData = territoryData;
        this.guiCallback = guiCallback;
    }

    @Override
    public void execute(Player player, String message) {
        int maxSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("TownDescSize");

        if(message.length() > maxSize){
            player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            return;
        }

        FileUtil.addLineToHistory(Lang.HISTORY_TOWN_MESSAGE_CHANGED.get(player.getName(), territoryData.getName(),message));
        territoryData.setDescription(message);
        player.sendMessage(ChatUtils.getTANString() + Lang.CHANGE_MESSAGE_SUCCESS.get());
        removePlayer(player);
        openGui(guiCallback,player);
    }
}
