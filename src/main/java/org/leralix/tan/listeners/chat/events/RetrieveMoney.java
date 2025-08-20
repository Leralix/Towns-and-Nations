package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.utils.text.TanChatUtils;

public class RetrieveMoney extends ChatListenerEvent {
    TerritoryData territoryData;
    public RetrieveMoney(TerritoryData territoryData) {
        this.territoryData = territoryData;
    }

    @Override
    public void execute(Player player, String message) {

        Double amount = parseStringToDouble(message);
        if(amount == null){
            player.sendMessage(TanChatUtils.getTANString() + Lang.SYNTAX_ERROR_AMOUNT.get());
            return;
        }

        if(amount > territoryData.getBalance()){
            player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.get());
            return;
        }
        territoryData.removeFromBalance(amount);
        EconomyUtil.addFromBalance(player, amount);

        player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_RETRIEVE_MONEY_SUCCESS.get(amount));
        SoundUtil.playSound(player, SoundEnum.MINOR_LEVEL_UP);
        PlayerChatListenerStorage.removePlayer(player);
    }
}
