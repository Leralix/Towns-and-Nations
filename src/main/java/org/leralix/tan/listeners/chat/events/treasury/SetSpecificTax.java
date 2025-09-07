package org.leralix.tan.listeners.chat.events.treasury;

import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.function.Consumer;

public abstract class SetSpecificTax extends ChatListenerEvent {

    private final Consumer<Player> guiCallback;


    protected SetSpecificTax(Consumer<Player> guiCallback) {
        this.guiCallback = guiCallback;
    }

    @Override
    public boolean execute(Player player, String message) {
        PlayerChatListenerStorage.removePlayer(player);
        Double amount = parseStringToDouble(message);
        if (amount == null) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.SYNTAX_ERROR_AMOUNT.get());
            return false;
        }
        if (amount < 0) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.VALUE_CANNOT_BE_NEGATIVE_ERROR.get());
            return false;
        }

        player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_SET_TAX_SUCCESS.get(amount));
        SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
        setTax(amount);

        openGui(guiCallback, player);
        return true;
    }

    protected abstract void setTax(double amount);
}
