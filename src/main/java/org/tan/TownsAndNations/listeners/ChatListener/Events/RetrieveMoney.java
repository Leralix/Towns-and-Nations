package org.tan.TownsAndNations.listeners.ChatListener.Events;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.Economy.EconomyUtil;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.listeners.ChatListener.ChatListenerEvent;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.SoundUtil;

import static org.tan.TownsAndNations.enums.MessageKey.TOWN_ID;
import static org.tan.TownsAndNations.enums.SoundEnum.MINOR_LEVEL_UP;
import static org.tan.TownsAndNations.listeners.ChatListener.PlayerChatListenerStorage.removePlayer;

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
