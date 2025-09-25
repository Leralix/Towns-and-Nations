package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;


public class DonateToTerritory extends ChatListenerEvent {

    TerritoryData territoryToDonate;

    public DonateToTerritory(TerritoryData territoryToDonate){
        super();
        this.territoryToDonate = territoryToDonate;
    }

    @Override
    public boolean execute(Player player, String message) {
        Double amount = parseStringToDouble(message);
        if (amount == null) {
            player.sendMessage(Lang.SYNTAX_ERROR_AMOUNT.get(player));
            return false;
        }
        territoryToDonate.addDonation(player, amount);
        return true;
    }
}
