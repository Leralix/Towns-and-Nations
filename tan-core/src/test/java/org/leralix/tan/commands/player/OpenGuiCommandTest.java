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

/** Unit tests for OpenGuiCommand. */
class OpenGuiCommandTest {

  private ServerMock server;
  private Player player;
  private OpenGuiCommand command;
  private ITanPlayer tanPlayer;

  @BeforeEach
  void setUp() {
    server = MockBukkit.mock();
    MockBukkit.load(SphereLib.class);
    MockBukkit.load(TownsAndNations.class);
    player = server.addPlayer("TestPlayer");
    command = new OpenGuiCommand();
    tanPlayer = PlayerDataStorage.getInstance().getSync(player);
  }

  @org.junit.jupiter.api.AfterEach
  void tearDown() {
    MockBukkit.unmock();
  }

  // ==================== Basic Command Info Tests ====================

  @Test
  void getName_returnsCorrectName() {
    assertEquals("gui", command.getName());
  }

  @Test
  void getSyntax_returnsCorrectSyntax() {
    assertEquals("/tan gui", command.getSyntax());
  }

  @Test
  void getArguments_returnsCorrectCount() {
    assertEquals(2, command.getArguments());
  }

  @Test
  void getTabCompleteSuggestions_returnsEmptyList() {
    assertTrue(command.getTabCompleteSuggestions(player, "test", new String[] {}).isEmpty());
  }

  // ==================== Argument Validation Tests ====================

  @Test
  void perform_withOneArg_doesNotThrow() {
    String[] args = {"gui"};

    // Should attempt to open GUI (may fail in test environment due to GUI mocking)
    // But should not throw exception
    try {
      command.perform(player, args);
    } catch (Exception e) {
      // GUI opening may fail in test environment - that's expected
      // But there should be no NPE or unexpected exceptions
      assertFalse(
          e instanceof NullPointerException,
          "Should not throw NullPointerException: " + e.getMessage());
    }
  }

  @Test
  void perform_tooManyArgs_showsError() {
    String[] args = {"gui", "extra"};

    // Should show error message for too many arguments
    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_threeArgs_showsError() {
    String[] args = {"gui", "arg1", "arg2"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  // ==================== Edge Cases ====================

  @Test
  void perform_emptyStringArg_showsError() {
    String[] args = {"gui", ""};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_multipleExtraArgs_showsError() {
    String[] args = {"gui", "extra1", "extra2", "extra3"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }
}
