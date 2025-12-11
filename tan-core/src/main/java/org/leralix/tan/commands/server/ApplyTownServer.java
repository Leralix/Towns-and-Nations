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
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.commands.CommandExceptionHandler;
import org.leralix.tan.utils.text.TanChatUtils;

public class ApplyTownServer extends SubCommand {

  @Override
  public String getName() {
    return "applytown";
  }

  @Override
  public String getDescription() {
    return Lang.APPLY_TOWN_SERVER_DESC.getDefault();
  }

  @Override
  public int getArguments() {
    return 3;
  }

  @Override
  public String getSyntax() {
    return "/coconationserver apply <town ID> <player username>";
  }

  @Override
  public List<String> getTabCompleteSuggestions(
      CommandSender commandSender, String currentMessage, String[] args) {
    return Collections.emptyList();
  }

  @Override
  public void perform(CommandSender commandSender, String[] args) {
    if (!CommandExceptionHandler.validateMinArgCount(commandSender, args, 3, getSyntax())) {
      return;
    }

    String townID = args[1];

    Optional<OfflinePlayer> offlinePlayerOpt =
        CommandExceptionHandler.findPlayer(commandSender, args[2]);
    if (offlinePlayerOpt.isEmpty()) {
      return;
    }

    Player p = offlinePlayerOpt.get().getPlayer();
    if (p == null) {
      TanChatUtils.message(commandSender, Lang.PLAYER_NOT_FOUND);
      return;
    }

    PlayerDataStorage.getInstance()
        .get(p.getUniqueId().toString())
        .thenAccept(
            tanPlayer -> {
              if (tanPlayer.hasTown()) {
                TanChatUtils.message(commandSender, Lang.PLAYER_ALREADY_HAVE_TOWN);
                return;
              }
              TownDataStorage.getInstance()
                  .get(townID)
                  .thenAccept(
                      townData -> {
                        if (townData == null) {
                          TanChatUtils.message(commandSender, Lang.TOWN_NOT_FOUND);
                          return;
                        }
                        townData.addPlayerJoinRequest(p);
                      });
            });
  }
}
