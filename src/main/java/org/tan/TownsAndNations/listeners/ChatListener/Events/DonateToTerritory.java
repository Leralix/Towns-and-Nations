package org.tan.TownsAndNations.listeners.ChatListener.Events;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.listeners.ChatListener.ChatListenerEvent;
import org.tan.TownsAndNations.utils.ChatUtils;

import static org.tan.TownsAndNations.listeners.ChatListener.PlayerChatListenerStorage.removePlayer;

public class DonateToTerritory extends ChatListenerEvent {

    ITerritoryData territoryToDonate;

    public DonateToTerritory(ITerritoryData territoryToDonate){
        super();
        this.territoryToDonate = territoryToDonate;
    }

    @Override
    public void execute(Player player, String message) {
        Integer amount = parseStringToInt(message);
        if (amount == null) {
            player.sendMessage(ChatUtils.getTANString() + Lang.SYNTAX_ERROR_AMOUNT.get());
            return;
        }
        removePlayer(player);
        territoryToDonate.addDonation(player, amount);
    }
}
