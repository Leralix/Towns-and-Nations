package org.leralix.tan.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.FoliaScheduler;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class CommandBlocker implements Listener {


    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        // Don't process if already cancelled
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        String inputCommand = event.getMessage();

        // Cancel the event first, we'll re-execute it if allowed
        event.setCancelled(true);

        // Check asynchronously if command should be blocked
        CompletableFuture<Boolean> attackCheckFuture = isPlayerInAnAttack(player, inputCommand);
        CompletableFuture<Boolean> relationCheckFuture = relationForbidCommandWithPlayer(player, inputCommand, Constants.getAllRelationBlacklistedCommands());

        // Combine both checks
        CompletableFuture.allOf(attackCheckFuture, relationCheckFuture).thenAccept(v -> {
            boolean shouldBlock = attackCheckFuture.join() || relationCheckFuture.join();

            if (!shouldBlock) {
                // Command is allowed, re-execute it on the entity scheduler
                FoliaScheduler.runEntityTask(TownsAndNations.getPlugin(), player, () -> {
                    player.performCommand(inputCommand.substring(1)); // Remove the leading "/"
                });
            }
            // If shouldBlock is true, command stays cancelled (already cancelled above)
        }).exceptionally(ex -> {
            TownsAndNations.getPlugin().getLogger().warning(
                "Error checking command permissions: " + ex.getMessage()
            );
            // On error, keep command cancelled for safety
            return null;
        });
    }

    /**
     * Detect if the input command is blocked depending on the relation between two players.
     *
     * @param sender The player executing the command.
     * @param inputCommand The raw command (ex: "/tan pay joe 10").
     * @return CompletableFuture<Boolean> indicating if the command is blocked
     */
    static CompletableFuture<Boolean> relationForbidCommandWithPlayer(Player sender, String inputCommand, Set<String> allBlacklistedCommands) {
        // Normalize command
        String normalizedInput = inputCommand.trim();
        String[] inputParts = normalizedInput.split(" ");

        // Create a list of futures for each blacklisted command check
        CompletableFuture<Boolean> resultFuture = CompletableFuture.completedFuture(false);

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

            // Chain async checks
            String finalSelectedPlayer = selectedPlayer;
            String finalBlackListedCommand = blackListedCommand;
            resultFuture = resultFuture.thenCompose(previousResult -> {
                if (previousResult) {
                    // Already blocked, short-circuit
                    return CompletableFuture.completedFuture(true);
                }

                // Fetch both player data asynchronously
                CompletableFuture<ITanPlayer> senderDataFuture = PlayerDataStorage.getInstance().get(sender);
                CompletableFuture<ITanPlayer> receiverDataFuture = PlayerDataStorage.getInstance().get(receiver);

                return senderDataFuture.thenCombine(receiverDataFuture, (senderData, receiverData) -> {
                    // Validate player data exists
                    if (senderData == null || receiverData == null) {
                        return false;
                    }

                    TownRelation worstRelationWithPlayer = senderData.getRelationWithPlayerSync(receiverData);
                    if(Constants.getRelationConstants(worstRelationWithPlayer).getBlockedCommands().contains(finalBlackListedCommand)){
                        LangType lang = senderData.getLang();
                        TanChatUtils.message(sender, Lang.CANNOT_CAST_COMMAND_ON_PLAYER_WITH_SPECIFIC_RELATION.get(lang, receiver.getName(),worstRelationWithPlayer.getColoredName(lang)), SoundEnum.NOT_ALLOWED);
                        return true;
                    }
                    return false;
                }).exceptionally(ex -> {
                    TownsAndNations.getPlugin().getLogger().warning(
                        "Error checking relation for player " + finalSelectedPlayer + ": " + ex.getMessage()
                    );
                    return false;
                });
            });
        }

        return resultFuture;
    }

    private static CompletableFuture<Boolean> isPlayerInAnAttack(Player player, String inputCommand) {
        return PlayerDataStorage.getInstance().get(player).thenApply(playerData -> {
            // Validate player data exists
            if (playerData == null) {
                return false;
            }

            if(!playerData.getAttackInvolvedIn().isEmpty()){
                for(String blackListedCommands : Constants.getBlacklistedCommandsDuringAttacks()){
                    if(inputCommand.startsWith(blackListedCommands)){
                        return true;
                    }
                }
            }
            return false;
        }).exceptionally(ex -> {
            TownsAndNations.getPlugin().getLogger().warning(
                "Error checking attack status: " + ex.getMessage()
            );
            return false;
        });
    }


}
