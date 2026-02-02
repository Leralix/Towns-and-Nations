package org.leralix.tan.listeners.chat;

import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.function.Consumer;

public class AdminSetTerritoryBalance extends ChatListenerEvent {

    private final TerritoryData territoryData;
    private final Consumer<Player> onComplete;

    public AdminSetTerritoryBalance(TerritoryData territoryData, Consumer<Player> onComplete) {
        this.territoryData = territoryData;
        this.onComplete = onComplete;
    }

    @Override
    protected boolean execute(Player player, ITanPlayer playerData, String message) {

        Double amount = parseStringToDouble(message);
        if (amount == null) {
            TanChatUtils.message(player, Lang.SYNTAX_ERROR_AMOUNT.get(playerData));
            return false;
        }

        territoryData.addToBalance(amount);

        SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
        openGui(onComplete, player);
        return true;
    }
}
