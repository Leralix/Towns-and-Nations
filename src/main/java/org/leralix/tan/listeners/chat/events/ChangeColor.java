package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.utils.StringUtil;
import org.leralix.tan.lang.Lang;

import java.util.function.Consumer;

public class ChangeColor extends ChatListenerEvent {
    private final TerritoryData territoryData;
    private final Consumer<Player> guiCallback;
    public ChangeColor(TerritoryData territoryData, Consumer<Player> guiCallback) {
        this.territoryData = territoryData;
        this.guiCallback = guiCallback;
    }

    @Override
    public void execute(Player player, String message) {
        if(!StringUtil.isValidColorCode(message)){
            player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_WRITE_NEW_COLOR_IN_CHAT_ERROR.get());
            return;
        }

        PlayerChatListenerStorage.removePlayer(player);
        territoryData.setChunkColor(StringUtil.hexColorToInt(message));
        player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_WRITE_NEW_COLOR_IN_CHAT_SUCCESS.get());
        openGui(guiCallback,player);
    }
}
