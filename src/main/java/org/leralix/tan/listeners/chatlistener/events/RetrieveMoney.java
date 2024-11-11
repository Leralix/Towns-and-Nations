package org.leralix.tan.listeners.chatlistener.events;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chatlistener.ChatListenerEvent;
import org.leralix.tan.utils.ChatUtils;
import org.leralix.tan.utils.SoundUtil;

import static org.leralix.tan.enums.SoundEnum.MINOR_LEVEL_UP;
import static org.leralix.tan.listeners.chatlistener.PlayerChatListenerStorage.removePlayer;

public class RetrieveMoney extends ChatListenerEvent {
    ITerritoryData territoryData;
    public RetrieveMoney(ITerritoryData territoryData) {
        this.territoryData = territoryData;
    }

    @Override
    public void execute(Player player, String message) {

        Integer amount = parseStringToInt(message);
        if(amount == null){
            player.sendMessage(ChatUtils.getTANString() + Lang.SYNTAX_ERROR_AMOUNT.get());
            return;
        }

        if(amount > territoryData.getBalance()){
            player.sendMessage(ChatUtils.getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.get());
            return;
        }
        territoryData.removeFromBalance(amount);
        EconomyUtil.addFromBalance(player, amount);

        player.sendMessage(ChatUtils.getTANString() + Lang.TOWN_RETRIEVE_MONEY_SUCCESS.get(amount));
        SoundUtil.playSound(player, MINOR_LEVEL_UP);
        removePlayer(player);
    }
}
