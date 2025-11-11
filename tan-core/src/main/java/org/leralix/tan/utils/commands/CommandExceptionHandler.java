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

/**
 * Utility class for handling exceptions in commands with proper error messages and logging.
 *
 * <p>This class provides standardized exception handling patterns for command execution, including:
 *
 * <ul>
 *   <li>Number parsing with error messages
 *   <li>Player lookups with error handling
 *   <li>Safe execution wrappers
 *   <li>Logging of command errors
 * </ul>
 */
public class CommandExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(CommandExceptionHandler.class);

  private CommandExceptionHandler() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Safely parses an integer from a string with error handling.
   *
   * @param sender The command sender to send error messages to
   * @param value The string value to parse
   * @param paramName The parameter name for error messages (e.g., "amount", "x coordinate")
   * @return Optional containing the parsed integer, or empty if parsing failed
   */
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

  /**
   * Safely parses a double from a string with error handling.
   *
   * @param sender The command sender to send error messages to
   * @param value The string value to parse
   * @param paramName The parameter name for error messages (e.g., "amount", "balance")
   * @return Optional containing the parsed double, or empty if parsing failed
   */
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

  /**
   * Safely looks up a player by name.
   *
   * @param sender The command sender to send error messages to
   * @param playerName The name of the player to look up
   * @return Optional containing the OfflinePlayer, or empty if player not found
   */
  public static Optional<OfflinePlayer> findPlayer(CommandSender sender, String playerName) {
    try {
      OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(playerName);

      // Check if player has ever played (exists in server records)
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

  /**
   * Safely retrieves TAN player data synchronously.
   *
   * @param sender The command sender to send error messages to
   * @param offlinePlayer The offline player to get data for
   * @return Optional containing the ITanPlayer, or empty if retrieval failed
   */
  public static Optional<ITanPlayer> getTanPlayer(
      CommandSender sender, OfflinePlayer offlinePlayer) {
    try {
      ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(offlinePlayer);
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

  /**
   * Safely executes a command operation with exception handling.
   *
   * @param sender The command sender
   * @param operation The operation to execute
   * @param errorMessage Custom error message to display on failure (null for default)
   * @return true if operation succeeded, false otherwise
   */
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

  /**
   * Validates that a command has the required number of arguments.
   *
   * @param sender The command sender to send error messages to
   * @param args The command arguments
   * @param expected The expected number of arguments
   * @param syntax The correct syntax string to display
   * @return true if validation passed, false otherwise
   */
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

  /**
   * Validates that a command has at least the minimum number of arguments.
   *
   * @param sender The command sender to send error messages to
   * @param args The command arguments
   * @param minimum The minimum number of arguments required
   * @param syntax The correct syntax string to display
   * @return true if validation passed, false otherwise
   */
  public static boolean validateMinArgCount(
      CommandSender sender, String[] args, int minimum, String syntax) {
    if (args.length < minimum) {
      TanChatUtils.message(sender, Lang.NOT_ENOUGH_ARGS_ERROR, SoundEnum.NOT_ALLOWED);
      TanChatUtils.message(sender, Lang.CORRECT_SYNTAX_INFO.get(syntax));
      return false;
    }
    return true;
  }

  /**
   * Validates that a command has at most the maximum number of arguments.
   *
   * @param sender The command sender to send error messages to
   * @param args The command arguments
   * @param maximum The maximum number of arguments allowed
   * @param syntax The correct syntax string to display
   * @return true if validation passed, false otherwise
   */
  public static boolean validateMaxArgCount(
      CommandSender sender, String[] args, int maximum, String syntax) {
    if (args.length > maximum) {
      TanChatUtils.message(sender, Lang.TOO_MANY_ARGS_ERROR, SoundEnum.NOT_ALLOWED);
      TanChatUtils.message(sender, Lang.CORRECT_SYNTAX_INFO.get(syntax));
      return false;
    }
    return true;
  }

  /**
   * Validates that a command has arguments within a range.
   *
   * @param sender The command sender to send error messages to
   * @param args The command arguments
   * @param min The minimum number of arguments required
   * @param max The maximum number of arguments allowed
   * @param syntax The correct syntax string to display
   * @return true if validation passed, false otherwise
   */
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

  /**
   * Validates that a player sender is online.
   *
   * @param sender The command sender
   * @return Optional containing the Player if online, or empty otherwise
   */
  public static Optional<Player> requirePlayer(CommandSender sender) {
    if (sender instanceof Player player) {
      return Optional.of(player);
    }
    sender.sendMessage("This command can only be executed by a player.");
    return Optional.empty();
  }

  /**
   * Logs a command execution for debugging purposes.
   *
   * @param sender The command sender
   * @param commandName The command name
   * @param args The command arguments
   */
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
