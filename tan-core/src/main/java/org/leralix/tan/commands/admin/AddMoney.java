package org.leralix.tan.commands.admin;

import java.util.List;
import java.util.Optional;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.leralix.lib.commands.SubCommand;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.commands.CommandExceptionHandler;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class AddMoney extends SubCommand {

  @Override
  public String getName() {
    return "addmoney";
  }

  @Override
  public String getDescription() {
    return Lang.ADMIN_ADD_MONEY_TO_PLAYER.getDefault();
  }

  @Override
  public int getArguments() {
    return 2;
  }

  @Override
  public String getSyntax() {
    return "/tanadmin addmoney <player> <amount>";
  }

  public List<String> getTabCompleteSuggestions(
      CommandSender commandSender, String lowerCase, String[] args) {
    return payPlayerSuggestion(args);
  }

  @Override
  public void perform(CommandSender commandSender, String[] args) {

    // Validate argument count
    if (!CommandExceptionHandler.validateArgCount(commandSender, args, 3, getSyntax())) {
      return;
    }

    // Find the target player
    Optional<OfflinePlayer> offlinePlayerOpt =
        CommandExceptionHandler.findPlayer(commandSender, args[1]);
    if (offlinePlayerOpt.isEmpty()) {
      return;
    }

    // Get TAN player data
    Optional<ITanPlayer> targetOpt =
        CommandExceptionHandler.getTanPlayer(commandSender, offlinePlayerOpt.get());
    if (targetOpt.isEmpty()) {
      return;
    }

    addMoney(commandSender, args, targetOpt.get());
  }

  static void addMoney(CommandSender commandSender, String[] args, ITanPlayer target) {
    // Parse amount with error handling
    Optional<Double> amountOpt =
        CommandExceptionHandler.parseDouble(commandSender, args[2], "amount");
    if (amountOpt.isEmpty()) {
      return;
    }

    double amount = amountOpt.get();

    try {
      EconomyUtil.addFromBalance(target, amount);
      TanChatUtils.message(
          commandSender,
          Lang.ADD_MONEY_COMMAND_SUCCESS.get(Double.toString(amount), target.getNameStored()));
      FileUtil.addLineToHistory(
          Lang.HISTORY_ADMIN_GIVE_MONEY.get(
              commandSender.getName(), Double.toString(amount), target.getNameStored()));
    } catch (Exception e) {
      TanChatUtils.message(commandSender, Lang.SYNTAX_ERROR, SoundEnum.NOT_ALLOWED);
      CommandExceptionHandler.logCommandExecution(commandSender, "addmoney", args);
    }
  }
}
