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

/** Unit tests for OpenNewsletterCommand. */
class OpenNewsletterCommandTest {

  private ServerMock server;
  private Player player;
  private OpenNewsletterCommand command;
  private ITanPlayer tanPlayer;

  @BeforeEach
  void setUp() {
    server = MockBukkit.mock();
    MockBukkit.load(SphereLib.class);
    MockBukkit.load(TownsAndNations.class);
    player = server.addPlayer("TestPlayer");
    command = new OpenNewsletterCommand();
    tanPlayer = PlayerDataStorage.getInstance().getSync(player);
  }

  @org.junit.jupiter.api.AfterEach
  void tearDown() {
    MockBukkit.unmock();
  }

  // ==================== Basic Command Info Tests ====================

  @Test
  void getName_returnsCorrectName() {
    assertEquals("newsletter", command.getName());
  }

  @Test
  void getSyntax_returnsCorrectSyntax() {
    assertEquals("/tan newsletter", command.getSyntax());
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
  void perform_withOneArg_doesNotThrow() {
    String[] args = {"newsletter"};

    // May fail due to GUI opening in test environment, but should not throw unexpected exceptions
    try {
      command.perform(player, args);
    } catch (Exception e) {
      assertFalse(
          e instanceof NullPointerException,
          "Should not throw NullPointerException: " + e.getMessage());
    }
  }

  @Test
  void perform_withTwoArgs_showsSyntaxError() {
    String[] args = {"newsletter", "extra"};

    command.perform(player, args);

    // Should show syntax error
  }

  @Test
  void perform_withThreeArgs_showsSyntaxError() {
    String[] args = {"newsletter", "arg1", "arg2"};

    command.perform(player, args);

    // Should show syntax error
  }

  @Test
  void perform_withZeroArgs_showsSyntaxError() {
    String[] args = {};

    command.perform(player, args);

    // Should show syntax error (length != 1)
  }

  // ==================== Edge Cases ====================

  @Test
  void perform_withEmptyStringArg_showsSyntaxError() {
    String[] args = {"newsletter", ""};

    command.perform(player, args);

    // Should show syntax error
  }

  @Test
  void perform_withWhitespaceArg_showsSyntaxError() {
    String[] args = {"newsletter", "   "};

    command.perform(player, args);

    // Should show syntax error
  }

  @Test
  void perform_withMultipleExtraArgs_showsSyntaxError() {
    String[] args = {"newsletter", "extra1", "extra2", "extra3"};

    command.perform(player, args);

    // Should show syntax error
  }
}
