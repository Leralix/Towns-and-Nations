package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.function.Consumer;

public class ChangeTerritoryDescription extends ChatListenerEvent {

    private final TerritoryData territoryData;
    Consumer<Player> guiCallback;

    public ChangeTerritoryDescription(@NotNull TerritoryData territoryData, Consumer<Player> guiCallback) {
        this.territoryData = territoryData;
        this.guiCallback = guiCallback;
    }

    @Override
    public boolean execute(Player player, String message) {

        int maxSize = territoryData instanceof TownData ?
                Constants.getTownMaxDescriptionSize() :
                Constants.getRegionMaxDescriptionSize();

        if (message.length() > maxSize) {
            TanChatUtils.message(player, Lang.MESSAGE_TOO_LONG.get(player, Integer.toString(maxSize)));
            return false;
        }

        FileUtil.addLineToHistory(Lang.HISTORY_TOWN_MESSAGE_CHANGED.get(player.getName(), territoryData.getName(), message));
        territoryData.setDescription(message);
        TanChatUtils.message(player, Lang.CHANGE_MESSAGE_SUCCESS.get(player));
        openGui(guiCallback, player);
        return true;
    }
}
