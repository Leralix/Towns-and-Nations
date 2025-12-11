package org.leralix.tan.commands.server;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.events.CreateTown;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.commands.CommandExceptionHandler;
import org.leralix.tan.utils.text.TanChatUtils;

class CreateTownServer extends SubCommand {

  @Override
  public String getName() {
    return "createtown";
  }

  @Override
  public String getDescription() {
    return Lang.CREATE_TOWN_SERVER_DESC.getDefault();
  }

  @Override
  public int getArguments() {
    return 2;
  }

  @Override
  public String getSyntax() {
    return "/coconationserver createtown <player_username> <town name>";
  }

  @Override
  public List<String> getTabCompleteSuggestions(
      CommandSender commandSender, String lowerCase, String[] args) {
    return Collections.emptyList();
  }

  @Override
  public void perform(CommandSender commandSender, String[] args) {
    if (!CommandExceptionHandler.validateMinArgCount(commandSender, args, 3, getSyntax())) {
      return;
    }

    Optional<OfflinePlayer> offlinePlayerOpt =
        CommandExceptionHandler.findPlayer(commandSender, args[1]);
    if (offlinePlayerOpt.isEmpty()) {
      return;
    }

    Player player = offlinePlayerOpt.get().getPlayer();
    if (player == null) {
      TanChatUtils.message(commandSender, Lang.PLAYER_NOT_FOUND);
      return;
    }

    StringBuilder townNameBuilder = new StringBuilder();
    for (int i = 2; i < args.length; i++) {
      townNameBuilder.append(args[i]).append(" ");
    }
    String townName = townNameBuilder.toString().trim();

    if (townName.length() < 3) {
      TanChatUtils.message(commandSender, Lang.INVALID_NAME);
      return;
    }
    if (townName.length() > 32) {
      TanChatUtils.message(commandSender, Lang.MESSAGE_TOO_LONG.get("32"));
      return;
    }

    if (TownDataStorage.getInstance().isNameUsed(townName)) {
      TanChatUtils.message(commandSender, Lang.NAME_ALREADY_USED);
      return;
    }

    new CreateTown(0).createTown(player, townName);
  }
}
