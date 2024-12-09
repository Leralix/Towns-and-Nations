package org.leralix.tan.listeners.chat.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.SoundEnum;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.utils.ChatUtils;
import org.leralix.tan.utils.SoundUtil;

public class SetSpecificRate extends ChatListenerEvent {

    TerritoryData territoryData;
    RateType rateType;

    public SetSpecificRate(TerritoryData territoryData, RateType rateType) {
        super();
        this.territoryData = territoryData;
        this.rateType = rateType;
    }
    @Override
    public void execute(Player player, String message) {
        PlayerChatListenerStorage.removePlayer(player);
        Double amount = parseStringToDouble(message);
        if(amount == null){
            player.sendMessage(ChatUtils.getTANString() + Lang.SYNTAX_ERROR_AMOUNT.get());
            return;
        }
        amount = Math.min(100, Math.max(0, amount));

        player.sendMessage(ChatUtils.getTANString() + Lang.TOWN_SET_RATE_SUCCESS.get(amount));
        SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);


        switch (rateType) {
            case RENT -> territoryData.setRentRate(amount/100);
            case BUY -> territoryData.setBuyRate(amount/100);
        }

        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> PlayerGUI.openTreasury(player, territoryData));
    }
}
