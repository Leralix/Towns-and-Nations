package org.leralix.tan.utils.commands;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.tan.TownsAndNations;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

/** Unit tests for CommandExceptionHandler utility class. */
class CommandExceptionHandlerTest {

  private ServerMock server;
  private CommandSender sender;

  @BeforeEach
  void setUp() {
    server = MockBukkit.mock();
    MockBukkit.load(SphereLib.class);
    MockBukkit.load(TownsAndNations.class);
    sender = server.addPlayer("TestPlayer");
  }

  @org.junit.jupiter.api.AfterEach
  void tearDown() {
    MockBukkit.unmock();
  }

  // ==================== parseInt Tests ====================

  @Test
  void parseInt_validInteger_returnsOptionalWithValue() {
    Optional<Integer> result = CommandExceptionHandler.parseInt(sender, "123", "amount");

    assertTrue(result.isPresent());
    assertEquals(123, result.get());
  }

  @Test
  void parseInt_negativeInteger_returnsOptionalWithValue() {
    Optional<Integer> result = CommandExceptionHandler.parseInt(sender, "-456", "amount");

    assertTrue(result.isPresent());
    assertEquals(-456, result.get());
  }

  @Test
  void parseInt_invalidString_returnsEmpty() {
    Optional<Integer> result = CommandExceptionHandler.parseInt(sender, "abc", "amount");

    assertTrue(result.isEmpty());
  }

  @Test
  void parseInt_emptyString_returnsEmpty() {
    Optional<Integer> result = CommandExceptionHandler.parseInt(sender, "", "amount");

    assertTrue(result.isEmpty());
  }

  @Test
  void parseInt_decimalNumber_returnsEmpty() {
    Optional<Integer> result = CommandExceptionHandler.parseInt(sender, "123.45", "amount");

    assertTrue(result.isEmpty());
  }

  @Test
  void parseInt_overflow_returnsEmpty() {
    Optional<Integer> result =
        CommandExceptionHandler.parseInt(sender, "9999999999999999", "amount");

    assertTrue(result.isEmpty());
  }

  // ==================== parseDouble Tests ====================

  @Test
  void parseDouble_validDouble_returnsOptionalWithValue() {
    Optional<Double> result = CommandExceptionHandler.parseDouble(sender, "123.45", "balance");

    assertTrue(result.isPresent());
    assertEquals(123.45, result.get(), 0.001);
  }

  @Test
  void parseDouble_integer_returnsOptionalWithValue() {
    Optional<Double> result = CommandExceptionHandler.parseDouble(sender, "100", "balance");

    assertTrue(result.isPresent());
    assertEquals(100.0, result.get(), 0.001);
  }

  @Test
  void parseDouble_negativeDouble_returnsOptionalWithValue() {
    Optional<Double> result = CommandExceptionHandler.parseDouble(sender, "-50.5", "balance");

    assertTrue(result.isPresent());
    assertEquals(-50.5, result.get(), 0.001);
  }

  @Test
  void parseDouble_invalidString_returnsEmpty() {
    Optional<Double> result = CommandExceptionHandler.parseDouble(sender, "abc", "balance");

    assertTrue(result.isEmpty());
  }

  @Test
  void parseDouble_emptyString_returnsEmpty() {
    Optional<Double> result = CommandExceptionHandler.parseDouble(sender, "", "balance");

    assertTrue(result.isEmpty());
  }

  // ==================== validateArgCount Tests ====================

  @Test
  void validateArgCount_exactMatch_returnsTrue() {
    String[] args = {"cmd", "arg1", "arg2"};

    boolean result = CommandExceptionHandler.validateArgCount(sender, args, 3, "/test syntax");

    assertTrue(result);
  }

  @Test
  void validateArgCount_tooFewArgs_returnsFalse() {
    String[] args = {"cmd", "arg1"};

    boolean result = CommandExceptionHandler.validateArgCount(sender, args, 3, "/test syntax");

    assertFalse(result);
  }

  @Test
  void validateArgCount_tooManyArgs_returnsFalse() {
    String[] args = {"cmd", "arg1", "arg2", "arg3", "arg4"};

    boolean result = CommandExceptionHandler.validateArgCount(sender, args, 3, "/test syntax");

    assertFalse(result);
  }

  // ==================== validateMinArgCount Tests ====================

  @Test
  void validateMinArgCount_exactMinimum_returnsTrue() {
    String[] args = {"cmd", "arg1", "arg2"};

    boolean result = CommandExceptionHandler.validateMinArgCount(sender, args, 3, "/test syntax");

    assertTrue(result);
  }

  @Test
  void validateMinArgCount_moreThanMinimum_returnsTrue() {
    String[] args = {"cmd", "arg1", "arg2", "arg3"};

    boolean result = CommandExceptionHandler.validateMinArgCount(sender, args, 3, "/test syntax");

    assertTrue(result);
  }

  @Test
  void validateMinArgCount_lessThanMinimum_returnsFalse() {
    String[] args = {"cmd", "arg1"};

    boolean result = CommandExceptionHandler.validateMinArgCount(sender, args, 3, "/test syntax");

    assertFalse(result);
  }

  // ==================== validateMaxArgCount Tests ====================

  @Test
  void validateMaxArgCount_exactMaximum_returnsTrue() {
    String[] args = {"cmd", "arg1", "arg2"};

    boolean result = CommandExceptionHandler.validateMaxArgCount(sender, args, 3, "/test syntax");

    assertTrue(result);
  }

  @Test
  void validateMaxArgCount_lessThanMaximum_returnsTrue() {
    String[] args = {"cmd", "arg1"};

    boolean result = CommandExceptionHandler.validateMaxArgCount(sender, args, 3, "/test syntax");

    assertTrue(result);
  }

  @Test
  void validateMaxArgCount_moreThanMaximum_returnsFalse() {
    String[] args = {"cmd", "arg1", "arg2", "arg3", "arg4"};

    boolean result = CommandExceptionHandler.validateMaxArgCount(sender, args, 3, "/test syntax");

    assertFalse(result);
  }

  // ==================== validateArgCountRange Tests ====================

  @Test
  void validateArgCountRange_withinRange_returnsTrue() {
    String[] args = {"cmd", "arg1", "arg2"};

    boolean result =
        CommandExceptionHandler.validateArgCountRange(sender, args, 2, 4, "/test syntax");

    assertTrue(result);
  }

  @Test
  void validateArgCountRange_atMinimum_returnsTrue() {
    String[] args = {"cmd", "arg1"};

    boolean result =
        CommandExceptionHandler.validateArgCountRange(sender, args, 2, 4, "/test syntax");

    assertTrue(result);
  }

  @Test
  void validateArgCountRange_atMaximum_returnsTrue() {
    String[] args = {"cmd", "arg1", "arg2", "arg3"};

    boolean result =
        CommandExceptionHandler.validateArgCountRange(sender, args, 2, 4, "/test syntax");

    assertTrue(result);
  }

  @Test
  void validateArgCountRange_belowMinimum_returnsFalse() {
    String[] args = {"cmd"};

    boolean result =
        CommandExceptionHandler.validateArgCountRange(sender, args, 2, 4, "/test syntax");

    assertFalse(result);
  }

  @Test
  void validateArgCountRange_aboveMaximum_returnsFalse() {
    String[] args = {"cmd", "arg1", "arg2", "arg3", "arg4", "arg5"};

    boolean result =
        CommandExceptionHandler.validateArgCountRange(sender, args, 2, 4, "/test syntax");

    assertFalse(result);
  }

  // ==================== requirePlayer Tests ====================

  @Test
  void requirePlayer_playerSender_returnsOptionalWithPlayer() {
    Player player = server.addPlayer("TestPlayer2");

    Optional<Player> result = CommandExceptionHandler.requirePlayer(player);

    assertTrue(result.isPresent());
    assertEquals(player, result.get());
  }

  @Test
  void requirePlayer_consoleSender_returnsEmpty() {
    CommandSender console = server.getConsoleSender();

    Optional<Player> result = CommandExceptionHandler.requirePlayer(console);

    assertTrue(result.isEmpty());
  }

  // ==================== findPlayer Tests ====================

  @Test
  void findPlayer_existingPlayer_returnsOptionalWithPlayer() {
    Player player = server.addPlayer("ExistingPlayer");

    Optional<OfflinePlayer> result = CommandExceptionHandler.findPlayer(sender, "ExistingPlayer");

    assertTrue(result.isPresent());
    assertEquals("ExistingPlayer", result.get().getName());
  }

  // ==================== safeExecute Tests ====================

  @Test
  void safeExecute_successfulOperation_returnsTrue() {
    boolean result =
        CommandExceptionHandler.safeExecute(
            sender,
            () -> {
              // Simulate successful operation
              return true;
            },
            null);

    assertTrue(result);
  }

  @Test
  void safeExecute_failedOperation_returnsFalse() {
    boolean result =
        CommandExceptionHandler.safeExecute(
            sender,
            () -> {
              // Simulate failed operation
              return false;
            },
            null);

    assertFalse(result);
  }

  @Test
  void safeExecute_exceptionThrown_returnsFalse() {
    boolean result =
        CommandExceptionHandler.safeExecute(
            sender,
            () -> {
              throw new RuntimeException("Test exception");
            },
            null);

    assertFalse(result);
  }

  // ==================== Edge Cases ====================

  @Test
  void parseInt_whitespace_returnsEmpty() {
    Optional<Integer> result = CommandExceptionHandler.parseInt(sender, "  123  ", "amount");

    // Should succeed because Integer.parseInt trims whitespace
    assertTrue(result.isPresent());
    assertEquals(123, result.get());
  }

  @Test
  void parseDouble_scientificNotation_returnsOptionalWithValue() {
    Optional<Double> result = CommandExceptionHandler.parseDouble(sender, "1.23e2", "amount");

    assertTrue(result.isPresent());
    assertEquals(123.0, result.get(), 0.001);
  }

  @Test
  void validateArgCount_emptyArgs_works() {
    String[] args = {};

    boolean result = CommandExceptionHandler.validateArgCount(sender, args, 0, "/test");

    assertTrue(result);
  }

  @Test
  void validateArgCountRange_sameMinMax_worksAsExactCount() {
    String[] args = {"cmd", "arg1", "arg2"};

    boolean result =
        CommandExceptionHandler.validateArgCountRange(sender, args, 3, 3, "/test syntax");

    assertTrue(result);
  }
}
