package org.leralix.tan.listeners.chat.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.TanChatUtils;
import org.leralix.tan.war.War;
import org.leralix.tan.war.legacy.WarRole;
import org.leralix.tan.war.legacy.wargoals.ConquerWarGoal;

public class SelectNbChunksForConquer extends ChatListenerEvent {

    private final War war;
    private final BasicGui fallbackGui;
    private final WarRole warRole;


    public SelectNbChunksForConquer(War war, WarRole warRole, BasicGui fallbackGui) {
        this.war = war;
        this.warRole = warRole;
        this.fallbackGui = fallbackGui;
    }

    @Override
    protected boolean execute(Player player, String message) {
        Integer amount = parseStringToInt(message);
        if (amount == null) {
            TanChatUtils.message(player, Lang.SYNTAX_ERROR_AMOUNT.get(player));
            return false;
        }

        int maxAmountOfChunkToCapture = Constants.getNbChunkToCaptureMax();
        if (amount > maxAmountOfChunkToCapture) {
            TanChatUtils.message(player, Lang.VALUE_EXCEED_MAXIMUM_ERROR.get(player, Integer.toString(maxAmountOfChunkToCapture)));
            return false;
        }

        war.addGoal(warRole, new ConquerWarGoal(amount));

        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), fallbackGui::open);
        SoundUtil.playSound(player, SoundEnum.MINOR_LEVEL_UP);
        return true;
    }
}
