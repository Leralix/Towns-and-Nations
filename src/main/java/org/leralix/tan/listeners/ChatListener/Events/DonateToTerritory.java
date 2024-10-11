package org.leralix.tan.listeners.ChatListener.Events;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.ChatListener.ChatListenerEvent;
import org.leralix.tan.utils.ChatUtils;

import static org.leralix.tan.listeners.ChatListener.PlayerChatListenerStorage.removePlayer;

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
