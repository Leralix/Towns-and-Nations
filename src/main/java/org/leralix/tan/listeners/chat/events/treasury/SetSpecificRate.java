package org.leralix.tan.listeners.chat.events.treasury;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.user.territory.TreasuryMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.utils.text.TanChatUtils;

public abstract class SetSpecificRate extends ChatListenerEvent {

    protected final TerritoryData territoryData;

    protected SetSpecificRate(TerritoryData territoryData) {
        super();
        this.territoryData = territoryData;
    }

    @Override
    public boolean execute(Player player, String message) {
        Double amount = parseStringToDouble(message);
        if (amount == null) {
            TanChatUtils.message(player, Lang.SYNTAX_ERROR_AMOUNT.get(player));
            return false;
        }
        amount = Math.min(100, Math.max(0, amount));

        TanChatUtils.message(player, Lang.TOWN_SET_RATE_SUCCESS.get(player, Double.toString(amount)), SoundEnum.MINOR_GOOD);


        setRate(amount / 100);

        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> new TreasuryMenu(player, territoryData));
        return false;
    }

    abstract void setRate(double percentage);
}
