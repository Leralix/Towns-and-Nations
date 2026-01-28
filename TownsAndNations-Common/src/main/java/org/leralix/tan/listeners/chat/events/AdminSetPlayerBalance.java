package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.gui.admin.AdminManagePlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.utils.text.TanChatUtils;

public class AdminSetPlayerBalance extends ChatListenerEvent {

    private final ITanPlayer targetPlayer;

    public AdminSetPlayerBalance(ITanPlayer targetPlayer) {
        super();
        this.targetPlayer = targetPlayer;
    }


    @Override
    protected boolean execute(Player player, String message) {
        Double amount = parseStringToDouble(message);
        if (amount == null) {
            TanChatUtils.message(player, Lang.SYNTAX_ERROR_AMOUNT.get(player));
            return false;
        }
        EconomyUtil.setBalance(targetPlayer, amount);
        SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
        openGui(p -> new AdminManagePlayer(player, targetPlayer), player);
        return true;
    }
}
