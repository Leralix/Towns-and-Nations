package org.leralix.tan.commands.player;

import java.util.List;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.exception.EconomyException;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.commands.CommandExceptionHandler;
import org.leralix.tan.utils.text.TanChatUtils;

public class PayCommand extends PlayerSubCommand {

  private final double maxPayDistance;

  public PayCommand(double maxPayDistance) {
    this.maxPayDistance = maxPayDistance;
  }

  @Override
  public String getName() {
    return "pay";
  }

  @Override
  public String getDescription() {
    return Lang.PAY_COMMAND_DESC.getDefault();
  }

  @Override
  public String getSyntax() {
    return "/tan pay <player> <amount>";
  }

  public int getArguments() {
    return 3;
  }

  @Override
  public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
    return payPlayerSuggestion(args);
  }

  @Override
  public void perform(Player player, String[] args) {
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    LangType langType = tanPlayer.getLang();

    // Validate argument count
    if (!CommandExceptionHandler.validateArgCount(player, args, 3, getSyntax())) {
      return;
    }

    // Find receiver
    Player receiver = Bukkit.getServer().getPlayer(args[1]);
    if (receiver == null) {
      TanChatUtils.message(player, Lang.PLAYER_NOT_FOUND.get(langType));
      return;
    }
    if (receiver.getUniqueId().equals(player.getUniqueId())) {
      TanChatUtils.message(player, Lang.PAY_SELF_ERROR.get(langType));
      return;
    }

    // Validate distance
    try {
      Location senderLocation = player.getLocation();
      Location receiverLocation = receiver.getLocation();
      if (senderLocation.getWorld() != receiverLocation.getWorld()) {
        TanChatUtils.message(player, Lang.INTERACTION_TOO_FAR_ERROR.get(langType));
        return;
      }
      if (senderLocation.distance(receiverLocation) > maxPayDistance) {
        TanChatUtils.message(player, Lang.INTERACTION_TOO_FAR_ERROR.get(langType));
        return;
      }
    } catch (Exception e) {
      TanChatUtils.message(player, Lang.SYNTAX_ERROR.get(langType));
      CommandExceptionHandler.logCommandExecution(player, "pay", args);
      return;
    }

    // Parse amount with error handling
    Optional<Integer> amountOpt = CommandExceptionHandler.parseInt(player, args[2], "amount");
    if (amountOpt.isEmpty()) {
      return;
    }

    int amount = amountOpt.get();
    if (amount < 1) {
      TanChatUtils.message(player, Lang.PAY_MINIMUM_REQUIRED.get(langType));
      return;
    }

    // Validate balance
    if (EconomyUtil.getBalance(player) < amount) {
      TanChatUtils.message(
          player,
          Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.get(
              langType, Double.toString(amount - EconomyUtil.getBalance(player))));
      return;
    }

    // Execute transaction with typed exception handling
    try {
      executePayment(player, receiver, amount, langType);
    } catch (EconomyException e) {
      // Economy-specific error (transaction failed, etc.)
      TanChatUtils.message(player, Lang.PLAYER_NOT_ENOUGH_MONEY.get(langType));
      CommandExceptionHandler.logCommandExecution(player, "pay", args);
    }
  }

  /**
   * Executes the payment transaction between two players.
   *
   * @param sender The player sending money
   * @param receiver The player receiving money
   * @param amount The amount to transfer
   * @param langType The language type for messages
   * @throws EconomyException If the transaction fails (insufficient funds, etc.)
   */
  private void executePayment(Player sender, Player receiver, int amount, LangType langType)
      throws EconomyException {
    try {
      EconomyUtil.removeFromBalance(sender, amount);
      EconomyUtil.addFromBalance(receiver, amount);

      TanChatUtils.message(
          sender,
          Lang.PAY_CONFIRMED_SENDER.get(langType, Integer.toString(amount), receiver.getName()));
      TanChatUtils.message(
          receiver,
          Lang.PAY_CONFIRMED_RECEIVER.get(receiver, Integer.toString(amount), sender.getName()));
    } catch (Exception e) {
      // Wrap generic exceptions into typed EconomyException
      throw new EconomyException("Payment transaction failed: " + e.getMessage(), e);
    }
  }
}
