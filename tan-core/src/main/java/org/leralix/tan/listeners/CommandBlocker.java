package org.leralix.tan.listeners;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan_java.performance.PlayerLangCache;

public class CommandBlocker implements Listener {

  @EventHandler
  public void onCommand(PlayerCommandPreprocessEvent event) {

    Player player = event.getPlayer();
    String inputCommand = event.getMessage();

    if (isPlayerInAnAttack(player, inputCommand)
        || relationForbidCommandWithPlayer(
            player, inputCommand, Constants.getAllRelationBlacklistedCommands())) {
      event.setCancelled(true);
    }
  }

  static boolean relationForbidCommandWithPlayer(
      Player sender, String inputCommand, Set<String> allBlacklistedCommands) {

    String normalizedInput = inputCommand.trim();
    String[] inputParts = normalizedInput.split(" ");

    for (String blackListedCommand : allBlacklistedCommands) {
      boolean nextCommand = false;
      String selectedPlayer = null;
      String[] blackListedParts = blackListedCommand.split(" ");
      if (blackListedParts.length > inputParts.length) {
        continue;
      }
      for (int i = 0; i < blackListedParts.length; i++) {

        if (blackListedParts[i].equals("%PLAYER%")) {
          selectedPlayer = inputParts[i];
        } else if (!blackListedParts[i].equals(inputParts[i])) {
          nextCommand = true;
          break;
        }
      }
      if (nextCommand || selectedPlayer == null) {
        continue;
      }
      Player receiver = Bukkit.getPlayer(selectedPlayer);
      if (receiver == null) {
        continue;
      }

      CompletableFuture<ITanPlayer> senderFuture = PlayerDataStorage.getInstance().get(sender);
      CompletableFuture<ITanPlayer> receiverFuture = PlayerDataStorage.getInstance().get(receiver);

      CompletableFuture.allOf(senderFuture, receiverFuture)
          .thenAccept(
              v -> {
                ITanPlayer senderData = senderFuture.join();
                ITanPlayer receiverData = receiverFuture.join();

                if (senderData == null || receiverData == null) {
                  return;
                }

                TownRelation worstRelationWithPlayer =
                    senderData.getRelationWithPlayerSync(receiverData);
                if (Constants.getRelationConstants(worstRelationWithPlayer)
                    .getBlockedCommands()
                    .contains(blackListedCommand)) {
                  PlayerLangCache.getInstance()
                      .getLang(sender)
                      .thenAccept(
                          lang -> {
                            TanChatUtils.message(
                                sender,
                                Lang.CANNOT_CAST_COMMAND_ON_PLAYER_WITH_SPECIFIC_RELATION.get(
                                    lang,
                                    receiver.getName(),
                                    worstRelationWithPlayer.getColoredName(lang)),
                                SoundEnum.NOT_ALLOWED);
                          });
                }
              });
      continue;
    }
    return false;
  }

  private static boolean isPlayerInAnAttack(Player player, String inputCommand) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            playerData -> {
              if (playerData != null && !playerData.getAttackInvolvedIn().isEmpty()) {
                for (String blackListedCommands : Constants.getBlacklistedCommandsDuringAttacks()) {
                  if (inputCommand.startsWith(blackListedCommands)) {
                    PlayerLangCache.getInstance()
                        .getLang(player)
                        .thenAccept(
                            lang -> {
                              TanChatUtils.message(
                                  player,
                                  Lang.CANNOT_CAST_COMMAND_DURING_ATTACK.get(lang),
                                  SoundEnum.NOT_ALLOWED);
                            });
                    return;
                  }
                }
              }
            });
    return false;
  }
}
