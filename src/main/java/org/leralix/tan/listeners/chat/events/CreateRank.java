package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.function.Consumer;

public class CreateRank extends ChatListenerEvent {
    private final TerritoryData territoryData;
    private final Consumer<Player> guiCallback;

    public CreateRank(TerritoryData townData, Consumer<Player> guiCallback) {
        this.territoryData = townData;
        this.guiCallback = guiCallback;
    }

    @Override
    public boolean execute(Player player, String message) {
        int maxNameSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("RankNameSize");

        if(message.length() > maxNameSize){
            player.sendMessage(TanChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxNameSize));
            return false;
        }
        if(territoryData.isRankNameUsed(message)){
            player.sendMessage(TanChatUtils.getTANString() + Lang.NAME_ALREADY_USED.get());
            return false;
        }

        territoryData.registerNewRank(message);
        openGui(guiCallback, player);
        return true;
    }
}
