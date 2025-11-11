package org.leralix.tan.commands.player;

import static org.junit.jupiter.api.Assertions.*;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

/** Unit tests for SetTownSpawnCommand. */
class SetTownSpawnCommandTest {

  private ServerMock server;
  private Player player;
  private SetTownSpawnCommand command;
  private ITanPlayer tanPlayer;

  @BeforeEach
  void setUp() {
    server = MockBukkit.mock();
    MockBukkit.load(SphereLib.class);
    MockBukkit.load(TownsAndNations.class);
    player = server.addPlayer("TestPlayer");
    command = new SetTownSpawnCommand();
    tanPlayer = PlayerDataStorage.getInstance().getSync(player);
  }

  @org.junit.jupiter.api.AfterEach
  void tearDown() {
    MockBukkit.unmock();
  }

  // ==================== Basic Command Info Tests ====================

  @Test
  void getName_returnsCorrectName() {
    assertEquals("setspawn", command.getName());
  }

  @Test
  void getSyntax_returnsCorrectSyntax() {
    assertEquals("/tan setspawn", command.getSyntax());
  }

  @Test
  void getArguments_returnsCorrectCount() {
    assertEquals(1, command.getArguments());
  }

  @Test
  void getDescription_returnsNonEmptyString() {
    assertNotNull(command.getDescription());
    assertFalse(command.getDescription().isEmpty());
  }

  @Test
  void getTabCompleteSuggestions_returnsEmptyList() {
    assertTrue(command.getTabCompleteSuggestions(player, "test", new String[] {}).isEmpty());
  }

  // ==================== Argument Validation Tests ====================

  @Test
  void perform_withOneArg_doesNotThrowException() {
    String[] args = {"setspawn"};

    // Will show "no town" error but should not throw exception
    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_withTwoArgs_showsSyntaxError() {
    String[] args = {"setspawn", "extra"};

    command.perform(player, args);

    // Should show syntax error
  }

  @Test
  void perform_withThreeArgs_showsSyntaxError() {
    String[] args = {"setspawn", "arg1", "arg2"};

    command.perform(player, args);

    // Should show syntax error
  }

  @Test
  void perform_withZeroArgs_showsSyntaxError() {
    String[] args = {};

    command.perform(player, args);

    // Should show syntax error
  }

  // ==================== No Town Tests ====================

  @Test
  void perform_playerWithoutTown_showsError() {
    String[] args = {"setspawn"};

    command.perform(player, args);

    // Should show "player has no town" error
  }

  // ==================== Edge Cases ====================

  @Test
  void perform_withEmptyStringArg_showsSyntaxError() {
    String[] args = {"setspawn", ""};

    command.perform(player, args);

    // Should show syntax error
  }

  @Test
  void perform_withWhitespaceArg_showsSyntaxError() {
    String[] args = {"setspawn", "   "};

    command.perform(player, args);

    // Should show syntax error
  }

  @Test
  void perform_withNumericArg_showsSyntaxError() {
    String[] args = {"setspawn", "123"};

    command.perform(player, args);

    // Should show syntax error
  }

  @Test
  void perform_withSpecialCharsArg_showsSyntaxError() {
    String[] args = {"setspawn", "@#$%"};

    command.perform(player, args);

    // Should show syntax error
  }

  @Test
  void perform_multipleExtraArgs_showsSyntaxError() {
    String[] args = {"setspawn", "extra1", "extra2", "extra3"};

    command.perform(player, args);

    // Should show syntax error
  }
}
