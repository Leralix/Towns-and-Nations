package org.leralix.tan.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.utils.text.TanChatUtils;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.war.PlannedAttack;
import org.leralix.tan.war.legacy.CurrentAttack;

import java.util.UUID;

public class CommandBlocker implements Listener {


    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {

        UUID playerID = event.getPlayer().getUniqueId();


        for(String blackListedCommands : Constants.getBlacklistedCommandsDuringAttacks()){
            if(blackListedCommands.startsWith(event.getMessage())){
                for(CurrentAttack attack : CurrentAttacksStorage.getAll()){
                    PlannedAttack plannedAttack = attack.getAttackData();
                    for(Player playerInWar : plannedAttack.getAllOnlinePlayers()){
                        if(playerInWar.getUniqueId().equals(playerID)){
                            event.setCancelled(true);
                            playerInWar.sendMessage(TanChatUtils.getTANString() + Lang.CANNOT_CAST_COMMAND_DURING_ATTACK.get());
                            return;
                        }
                    }
                }
            }
        }
    }
}
