package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.utils.text.StringUtil;

import java.util.function.Consumer;

public class ChangeColor extends ChatListenerEvent {
    private final TerritoryData territoryData;
    private final Consumer<Player> guiCallback;
    public ChangeColor(TerritoryData territoryData, Consumer<Player> guiCallback) {
        this.territoryData = territoryData;
        this.guiCallback = guiCallback;
    }

    @Override
    public boolean execute(Player player, String message) {
        if(!StringUtil.isValidColorCode(message)){
            player.sendMessage(Lang.GUI_TOWN_SETTINGS_WRITE_NEW_COLOR_IN_CHAT_ERROR.get(player));
            return false;
        }

        territoryData.setChunkColor(StringUtil.hexColorToInt(message));
        player.sendMessage(Lang.GUI_TOWN_SETTINGS_WRITE_NEW_COLOR_IN_CHAT_SUCCESS.get(player));
        openGui(guiCallback,player);
        return true;
    }
}
