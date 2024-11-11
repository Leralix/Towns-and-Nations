package org.leralix.tan.listeners.chatlistener.events;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chatlistener.ChatListenerEvent;
import org.leralix.tan.utils.ChatUtils;
import org.leralix.tan.utils.StringUtil;

import java.util.function.Consumer;

import static org.leralix.tan.listeners.chatlistener.PlayerChatListenerStorage.removePlayer;
import static org.leralix.tan.utils.StringUtil.hexColorToInt;

public class ChangeColor extends ChatListenerEvent {
    private final ITerritoryData territoryData;
    private final Consumer<Player> guiCallback;
    public ChangeColor(ITerritoryData territoryData, Consumer<Player> guiCallback) {
        this.territoryData = territoryData;
        this.guiCallback = guiCallback;
    }

    @Override
    public void execute(Player player, String message) {
        removePlayer(player);

        if(!StringUtil.isValidColorCode(message)){
            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_WRITE_NEW_COLOR_IN_CHAT_ERROR.get());
            return;
        }
        territoryData.setChunkColor(hexColorToInt(message));
        player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_WRITE_NEW_COLOR_IN_CHAT_SUCCESS.get());
        openGui(guiCallback,player);
    }
}
