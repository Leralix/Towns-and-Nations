package org.leralix.tan.commands.admin;

import java.util.List;
import java.util.Optional;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.leralix.lib.commands.SubCommand;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.exception.EconomyException;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.commands.CommandExceptionHandler;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class SetMoney extends SubCommand {

  @Override
  public String getName() {
    return "setmoney";
  }

  @Override
  public String getDescription() {
    return Lang.ADMIN_SET_PLAYER_MONEY_BALANCE.getDefault();
  }

  @Override
  public int getArguments() {
    return 2;
  }

  @Override
  public String getSyntax() {
    return "/coconationadmin setmoney <player> <amount>";
  }

  public List<String> getTabCompleteSuggestions(
      CommandSender player, String lowerCase, String[] args) {
    return payPlayerSuggestion(args);
  }

  @Override
  public void perform(CommandSender player, String[] args) {
    if (!CommandExceptionHandler.validateArgCount(player, args, 3, getSyntax())) {
      return;
    }

    Optional<OfflinePlayer> offlinePlayerOpt = CommandExceptionHandler.findPlayer(player, args[1]);
    if (offlinePlayerOpt.isEmpty()) {
      return;
    }

    Optional<ITanPlayer> targetOpt =
        CommandExceptionHandler.getTanPlayer(player, offlinePlayerOpt.get());
    if (targetOpt.isEmpty()) {
      return;
    }

    setMoney(player, args, targetOpt.get());
  }

  static void setMoney(CommandSender commandSender, String[] args, ITanPlayer target) {
    Optional<Double> amountOpt =
        CommandExceptionHandler.parseDouble(commandSender, args[2], "amount");
    if (amountOpt.isEmpty()) {
      return;
    }

    double amount = amountOpt.get();

    try {
      executeSetMoney(target, amount);

      TanChatUtils.message(
          commandSender,
          Lang.SET_MONEY_COMMAND_SUCCESS.get(Double.toString(amount), target.getNameStored()));
      FileUtil.addLineToHistory(
          Lang.HISTORY_ADMIN_SET_MONEY.get(
              commandSender.getName(), Double.toString(amount), target.getNameStored()));
    } catch (EconomyException e) {
      TanChatUtils.message(commandSender, Lang.SYNTAX_ERROR, SoundEnum.NOT_ALLOWED);
      CommandExceptionHandler.logCommandExecution(commandSender, "setmoney", args);
    }
  }

  private static void executeSetMoney(ITanPlayer target, double amount) throws EconomyException {
    try {
      EconomyUtil.setBalance(target, amount);
    } catch (Exception e) {
      throw new EconomyException("Set money operation failed: " + e.getMessage(), e);
    }
  }
}
