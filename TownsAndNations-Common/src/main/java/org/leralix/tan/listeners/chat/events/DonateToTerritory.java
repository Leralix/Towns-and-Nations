package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.utils.text.TanChatUtils;


public class DonateToTerritory extends ChatListenerEvent {

    TerritoryData territoryToDonate;

    public DonateToTerritory(TerritoryData territoryToDonate) {
        super();
        this.territoryToDonate = territoryToDonate;
    }

    @Override
    public boolean execute(Player player, String message) {
        Double amount = parseStringToDouble(message);
        if (amount == null) {
            TanChatUtils.message(player, Lang.SYNTAX_ERROR_AMOUNT.get(player));
            return false;
        }
        territoryToDonate.addDonation(player, amount);
        return true;
    }
}
