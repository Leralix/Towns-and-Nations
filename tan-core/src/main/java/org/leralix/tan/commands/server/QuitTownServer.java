package org.leralix.tan.commands.server;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.commands.CommandExceptionHandler;
import org.leralix.tan.utils.text.TanChatUtils;

class QuitTownServer extends SubCommand {

  @Override
  public String getName() {
    return "quittown";
  }

  @Override
  public String getDescription() {
    return Lang.QUIT_TOWN_SERVER_DESC.getDefault();
  }

  @Override
  public int getArguments() {
    return 2;
  }

  @Override
  public String getSyntax() {
    return "/tanserver quittown <player_username>";
  }

  @Override
  public List<String> getTabCompleteSuggestions(
      CommandSender commandSender, String currentMessage, String[] args) {
    return Collections.emptyList();
  }

  @Override
  public void perform(CommandSender commandSender, String[] args) {
    // Validate argument count
    if (!CommandExceptionHandler.validateArgCount(commandSender, args, 2, getSyntax())) {
      return;
    }

    // Find and validate player
    Optional<OfflinePlayer> offlinePlayerOpt =
        CommandExceptionHandler.findPlayer(commandSender, args[1]);
    if (offlinePlayerOpt.isEmpty()) {
      return;
    }

    // Check if player is online
    Player p = offlinePlayerOpt.get().getPlayer();
    if (p == null) {
      TanChatUtils.message(commandSender, Lang.PLAYER_NOT_FOUND);
      return;
    }

    // Async: Get player data and remove from town
    PlayerDataStorage.getInstance()
        .get(p)
        .thenAccept(
            tanPlayer -> {
              tanPlayer
                  .getTown()
                  .thenAccept(
                      townData -> {
                        if (townData == null) {
                          TanChatUtils.message(commandSender, Lang.PLAYER_NO_TOWN);
                          return;
                        }
                        if (townData.isLeader(tanPlayer)) {
                          TanChatUtils.message(commandSender, Lang.LEADER_CANNOT_QUIT_TOWN);
                          return;
                        }
                        townData.removePlayer(tanPlayer);
                      });
            });
  }
}
