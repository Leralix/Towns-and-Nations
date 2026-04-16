package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Nation;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.function.Consumer;

public class ChangeTerritoryDescription extends ChatListenerEvent {

    private final Territory territoryData;
    Consumer<Player> guiCallback;

    public ChangeTerritoryDescription(@NotNull Territory territoryData, Consumer<Player> guiCallback) {
        this.territoryData = territoryData;
        this.guiCallback = guiCallback;
    }

    @Override
    public boolean execute(Player player, ITanPlayer playerData, String message) {

        int maxSize;
        if (territoryData instanceof Town) {
            maxSize = Constants.getTownMaxDescriptionSize();
        } else if (territoryData instanceof Nation) {
            maxSize = Constants.getNationMaxDescriptionSize();
        } else {
            maxSize = Constants.getRegionMaxDescriptionSize();
        }

        if (message.length() > maxSize) {
            TanChatUtils.message(player, Lang.MESSAGE_TOO_LONG.get(playerData, Integer.toString(maxSize)));
            return false;
        }

        if (territoryData instanceof Town) {
            FileUtil.addLineToHistory(Lang.HISTORY_TOWN_MESSAGE_CHANGED.get(player.getName(), territoryData.getName(), message));
        } else if (territoryData instanceof Nation) {
            FileUtil.addLineToHistory(Lang.HISTORY_NATION_MESSAGE_CHANGED.get(player.getName(), territoryData.getName(), message));
        } else {
            FileUtil.addLineToHistory(Lang.HISTORY_REGION_MESSAGE_CHANGED.get(player.getName(), territoryData.getName(), message));
        }
        territoryData.setDescription(message);
        TanChatUtils.message(player, Lang.CHANGE_MESSAGE_SUCCESS.get(playerData));
        openGui(guiCallback, player);
        return true;
    }
}
