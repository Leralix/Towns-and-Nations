package org.leralix.tan.listeners.chat.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.user.territory.TreasuryMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.utils.TanChatUtils;

public class SetSpecificTax extends ChatListenerEvent {

    TerritoryData territoryData;

    public SetSpecificTax(TerritoryData territoryData) {
        super();
        this.territoryData = territoryData;
    }
    @Override
    public void execute(Player player, String message) {
        PlayerChatListenerStorage.removePlayer(player);
        Double amount = parseStringToDouble(message);
        if(amount == null){
            player.sendMessage(TanChatUtils.getTANString() + Lang.SYNTAX_ERROR_AMOUNT.get());
            return;
        }
        amount = Math.max(0, amount);

        player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_SET_TAX_SUCCESS.get(amount));
        SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
        territoryData.setTax(amount);

        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> new TreasuryMenu(player, territoryData));
    }
}
