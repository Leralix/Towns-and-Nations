package org.leralix.tan.listeners.chatlistener.events;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chatlistener.ChatListenerEvent;
import org.leralix.tan.utils.ChatUtils;

import static org.leralix.tan.listeners.chatlistener.PlayerChatListenerStorage.removePlayer;

public class DonateToTerritory extends ChatListenerEvent {

    TerritoryData territoryToDonate;

    public DonateToTerritory(TerritoryData territoryToDonate){
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
