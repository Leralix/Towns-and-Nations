package org.leralix.tan.listeners.chat.events.treasury;

import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.function.Consumer;

public abstract class SetSpecificTax extends ChatListenerEvent {

    private final Consumer<Player> guiCallback;


    protected SetSpecificTax(Consumer<Player> guiCallback) {
        this.guiCallback = guiCallback;
    }

    @Override
    public boolean execute(Player player, String message) {
        Double amount = parseStringToDouble(message);
        if (amount == null) {
            TanChatUtils.message(player, Lang.SYNTAX_ERROR_AMOUNT.get(player));
            return false;
        }
        if (amount < 0) {
            TanChatUtils.message(player, Lang.VALUE_CANNOT_BE_NEGATIVE_ERROR.get(player));
            return false;
        }

        TanChatUtils.message(player, Lang.TOWN_SET_TAX_SUCCESS.get(player, Double.toString(amount)), SoundEnum.MINOR_GOOD);
        setTax(amount);

        openGui(guiCallback, player);
        return true;
    }

    protected abstract void setTax(double amount);
}
