package org.leralix.tan.listeners.chat.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.SoundEnum;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.utils.ChatUtils;
import org.leralix.tan.utils.SoundUtil;

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
            player.sendMessage(ChatUtils.getTANString() + Lang.SYNTAX_ERROR_AMOUNT.get());
            return;
        }
        amount = Math.max(0, amount);

        player.sendMessage(ChatUtils.getTANString() + Lang.TOWN_SET_TAX_SUCCESS.get(amount));
        SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
        territoryData.setTax(amount);

        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> PlayerGUI.openTreasury(player, territoryData));
    }
}
