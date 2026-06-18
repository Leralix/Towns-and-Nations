package org.leralix.tan.listeners.chat.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.TanChatUtils;
import org.leralix.tan.war.War;
import org.leralix.tan.war.info.WarRole;
import org.leralix.tan.war.wargoals.TributeWarGoal;

public class SelectQuantityForTribute extends ChatListenerEvent {

    private final War war;
    private final BasicGui fallbackGui;
    private final WarRole warRole;


    public SelectQuantityForTribute(War war, WarRole warRole, BasicGui fallbackGui) {
        this.war = war;
        this.warRole = warRole;
        this.fallbackGui = fallbackGui;
    }

    @Override
    protected boolean execute(Player player, ITanPlayer playerData, String message) {
        Integer amount = parseStringToInt(message);
        if (amount == null) {
            TanChatUtils.message(player, Lang.SYNTAX_ERROR_AMOUNT.get(playerData));
            return false;
        }

        double maxAmountOfChunkToCapture = Constants.getMaxTributeAmount();
        if (amount > maxAmountOfChunkToCapture) {
            TanChatUtils.message(player, Lang.VALUE_EXCEED_MAXIMUM_ERROR.get(playerData, Double.toString(maxAmountOfChunkToCapture)));
            return false;
        }

        war.addGoal(warRole, new TributeWarGoal(amount));

        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), fallbackGui::open);
        SoundUtil.playSound(player, SoundEnum.MINOR_LEVEL_UP);
        return true;
    }
}
