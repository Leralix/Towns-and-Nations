package org.leralix.tan.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.Set;

public class CommandBlocker implements Listener {


    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {

        Player player = event.getPlayer();
        String inputCommand = event.getMessage();

        if(isPlayerInAnAttack(player, inputCommand) || relationForbidCommandWithPlayer(player, inputCommand, Constants.getAllRelationBlacklistedCommands())){
            event.setCancelled(true);
        }
    }

    /**
     * Detect if the input command is blocked depending on the relation between two players.
     *
     * @param sender The player executing the command.
     * @param inputCommand The raw command (ex: "/tan pay joe 10").
     * @return The target player name if the command is blocked, otherwise null.
     */
    static boolean relationForbidCommandWithPlayer(Player sender, String inputCommand, Set<String> allBlacklistedCommands) {

        // Normalize command
        String normalizedInput = inputCommand.trim();
        String[] inputParts = normalizedInput.split(" ");


        for(String blackListedCommand : allBlacklistedCommands){
            boolean nextCommand = false;
            String selectedPlayer = null;
            String[] blackListedParts = blackListedCommand.split(" ");
            if(blackListedParts.length > inputParts.length){
                continue;
            }
            for(int i = 0; i < blackListedParts.length; i++){

                if(blackListedParts[i].equals("%PLAYER%")){
                    selectedPlayer = inputParts[i];
                }
                else if(!blackListedParts[i].equals(inputParts[i])){
                    nextCommand = true;
                    break;
                }
            }
            if(nextCommand || selectedPlayer == null){
                continue;
            }
            Player receiver = Bukkit.getPlayer(selectedPlayer);
            if(receiver == null){
                continue;
            }

            ITanPlayer senderData = PlayerDataStorage.getInstance().get(sender);
            ITanPlayer receiverData = PlayerDataStorage.getInstance().get(receiver);
            TownRelation worstRelationWithPlayer = senderData.getRelationWithPlayer(receiverData);
            if(Constants.getRelationConstants(worstRelationWithPlayer).getBlockedCommands().contains(blackListedCommand)){
                LangType lang = senderData.getLang();
                sender.sendMessage(Lang.CANNOT_CAST_COMMAND_ON_PLAYER_WITH_SPECIFIC_RELATION.get(lang, receiver.getName(),worstRelationWithPlayer.getColoredName(lang)));
                return true;
            }
        }
        return false;
    }

    private static boolean isPlayerInAnAttack(Player player, String inputCommand) {

        if(PlayerDataStorage.getInstance().get(player).getAttackInvolvedIn().isEmpty()){
            for(String blackListedCommands : Constants.getBlacklistedCommandsDuringAttacks()){
                if(blackListedCommands.startsWith(inputCommand)){
                    return true;
                }
            }
        }
        return false;
    }


}
