package org.leralix.tan.utils.commands;

import java.util.Optional;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(CommandExceptionHandler.class);

  private CommandExceptionHandler() {
    throw new IllegalStateException("Utility class");
  }

  public static Optional<Integer> parseInt(CommandSender sender, String value, String paramName) {
    try {
      return Optional.of(Integer.parseInt(value));
    } catch (NumberFormatException e) {
      TanChatUtils.message(sender, Lang.SYNTAX_ERROR_AMOUNT, SoundEnum.NOT_ALLOWED);
      logger.debug(
          "Failed to parse integer parameter '{}' from value: {} (Command sender: {})",
          paramName,
          value,
          sender.getName());
      return Optional.empty();
    }
  }

  public static Optional<Double> parseDouble(CommandSender sender, String value, String paramName) {
    try {
      return Optional.of(Double.parseDouble(value));
    } catch (NumberFormatException e) {
      TanChatUtils.message(sender, Lang.SYNTAX_ERROR_AMOUNT, SoundEnum.NOT_ALLOWED);
      logger.debug(
          "Failed to parse double parameter '{}' from value: {} (Command sender: {})",
          paramName,
          value,
          sender.getName());
      return Optional.empty();
    }
  }

  public static Optional<OfflinePlayer> findPlayer(CommandSender sender, String playerName) {
    try {
      OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(playerName);

      if (offlinePlayer.getName() == null && !offlinePlayer.hasPlayedBefore()) {
        TanChatUtils.message(sender, Lang.PLAYER_NOT_FOUND, SoundEnum.NOT_ALLOWED);
        logger.debug("Player not found: {} (Command sender: {})", playerName, sender.getName());
        return Optional.empty();
      }

      return Optional.of(offlinePlayer);
    } catch (Exception e) {
      TanChatUtils.message(sender, Lang.PLAYER_NOT_FOUND, SoundEnum.NOT_ALLOWED);
      logger.error(
          "Exception while looking up player: {} (Command sender: {})",
          playerName,
          sender.getName(),
          e);
      return Optional.empty();
    }
  }

  public static Optional<ITanPlayer> getTanPlayer(
      CommandSender sender, OfflinePlayer offlinePlayer) {
    try {
      ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(offlinePlayer).join();
      if (tanPlayer == null) {
        TanChatUtils.message(sender, Lang.PLAYER_NOT_FOUND, SoundEnum.NOT_ALLOWED);
        logger.warn(
            "TAN player data not found for: {} (Command sender: {})",
            offlinePlayer.getName(),
            sender.getName());
        return Optional.empty();
      }
      return Optional.of(tanPlayer);
    } catch (Exception e) {
      TanChatUtils.message(sender, Lang.PLAYER_NOT_FOUND, SoundEnum.NOT_ALLOWED);
      logger.error(
          "Exception while retrieving TAN player data for: {} (Command sender: {})",
          offlinePlayer.getName(),
          sender.getName(),
          e);
      return Optional.empty();
    }
  }

  public static boolean safeExecute(
      CommandSender sender, Supplier<Boolean> operation, Lang errorMessage) {
    try {
      return operation.get();
    } catch (Exception e) {
      if (errorMessage != null) {
        TanChatUtils.message(sender, errorMessage, SoundEnum.NOT_ALLOWED);
      } else {
        TanChatUtils.message(sender, Lang.SYNTAX_ERROR, SoundEnum.NOT_ALLOWED);
      }
      logger.error("Exception during command execution (Command sender: {})", sender.getName(), e);
      return false;
    }
  }

  public static boolean validateArgCount(
      CommandSender sender, String[] args, int expected, String syntax) {
    if (args.length < expected) {
      TanChatUtils.message(sender, Lang.NOT_ENOUGH_ARGS_ERROR, SoundEnum.NOT_ALLOWED);
      TanChatUtils.message(sender, Lang.CORRECT_SYNTAX_INFO.get(syntax));
      return false;
    } else if (args.length > expected) {
      TanChatUtils.message(sender, Lang.TOO_MANY_ARGS_ERROR, SoundEnum.NOT_ALLOWED);
      TanChatUtils.message(sender, Lang.CORRECT_SYNTAX_INFO.get(syntax));
      return false;
    }
    return true;
  }

  public static boolean validateMinArgCount(
      CommandSender sender, String[] args, int minimum, String syntax) {
    if (args.length < minimum) {
      TanChatUtils.message(sender, Lang.NOT_ENOUGH_ARGS_ERROR, SoundEnum.NOT_ALLOWED);
      TanChatUtils.message(sender, Lang.CORRECT_SYNTAX_INFO.get(syntax));
      return false;
    }
    return true;
  }

  public static boolean validateMaxArgCount(
      CommandSender sender, String[] args, int maximum, String syntax) {
    if (args.length > maximum) {
      TanChatUtils.message(sender, Lang.TOO_MANY_ARGS_ERROR, SoundEnum.NOT_ALLOWED);
      TanChatUtils.message(sender, Lang.CORRECT_SYNTAX_INFO.get(syntax));
      return false;
    }
    return true;
  }

  public static boolean validateArgCountRange(
      CommandSender sender, String[] args, int min, int max, String syntax) {
    if (args.length < min) {
      TanChatUtils.message(sender, Lang.NOT_ENOUGH_ARGS_ERROR, SoundEnum.NOT_ALLOWED);
      TanChatUtils.message(sender, Lang.CORRECT_SYNTAX_INFO.get(syntax));
      return false;
    } else if (args.length > max) {
      TanChatUtils.message(sender, Lang.TOO_MANY_ARGS_ERROR, SoundEnum.NOT_ALLOWED);
      TanChatUtils.message(sender, Lang.CORRECT_SYNTAX_INFO.get(syntax));
      return false;
    }
    return true;
  }

  public static Optional<Player> requirePlayer(CommandSender sender) {
    if (sender instanceof Player player) {
      return Optional.of(player);
    }
    sender.sendMessage("This command can only be executed by a player.");
    return Optional.empty();
  }

  public static void logCommandExecution(CommandSender sender, String commandName, String[] args) {
    if (logger.isDebugEnabled()) {
      logger.debug(
          "Command executed: /{} {} (Sender: {})",
          commandName,
          String.join(" ", args),
          sender.getName());
    }
  }
}
