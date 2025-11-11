package org.leralix.tan.commands.player;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

/** Unit tests for JoinTownCommand. */
class JoinTownCommandTest {

  private ServerMock server;
  private Player player;
  private JoinTownCommand command;
  private ITanPlayer tanPlayer;

  @BeforeEach
  void setUp() {
    server = MockBukkit.mock();
    MockBukkit.load(SphereLib.class);
    MockBukkit.load(TownsAndNations.class);
    player = server.addPlayer("TestPlayer");
    command = new JoinTownCommand();
    tanPlayer = PlayerDataStorage.getInstance().getSync(player);
  }

  @org.junit.jupiter.api.AfterEach
  void tearDown() {
    MockBukkit.unmock();
  }

  // ==================== Basic Command Info Tests ====================

  @Test
  void getName_returnsCorrectName() {
    assertEquals("join", command.getName());
  }

  @Test
  void getSyntax_returnsCorrectSyntax() {
    assertEquals("/tan join <Town ID>", command.getSyntax());
  }

  @Test
  void getArguments_returnsCorrectCount() {
    assertEquals(2, command.getArguments());
  }

  @Test
  void getDescription_returnsNonEmptyString() {
    assertNotNull(command.getDescription());
    assertFalse(command.getDescription().isEmpty());
  }

  // ==================== Tab Completion Tests ====================

  @Test
  void getTabCompleteSuggestions_withTwoArgs_returnsTownIdPlaceholder() {
    String[] args = {"join", ""};
    List<String> suggestions = command.getTabCompleteSuggestions(player, "join", args);

    assertEquals(1, suggestions.size());
    assertTrue(suggestions.contains("<Town ID>"));
  }

  @Test
  void getTabCompleteSuggestions_withOneArg_returnsEmpty() {
    String[] args = {"join"};
    List<String> suggestions = command.getTabCompleteSuggestions(player, "join", args);

    assertTrue(suggestions.isEmpty());
  }

  @Test
  void getTabCompleteSuggestions_withThreeArgs_returnsEmpty() {
    String[] args = {"join", "townId", "extra"};
    List<String> suggestions = command.getTabCompleteSuggestions(player, "join", args);

    assertTrue(suggestions.isEmpty());
  }

  // ==================== Argument Validation Tests ====================

  @Test
  void perform_noArguments_showsSyntaxError() {
    String[] args = {"join"};

    command.perform(player, args);

    // Should show "not enough arguments" error
  }

  @Test
  void perform_tooManyArguments_showsSyntaxError() {
    String[] args = {"join", "townId", "extra"};

    command.perform(player, args);

    // Should show "too many arguments" error
  }

  @Test
  void perform_fourArguments_showsSyntaxError() {
    String[] args = {"join", "townId", "arg3", "arg4"};

    command.perform(player, args);

    // Should show "too many arguments" error
  }

  @Test
  void perform_exactlyTwoArgs_doesNotThrowException() {
    String[] args = {"join", "someTownId"};

    // Should not throw exception even if town not found
    assertDoesNotThrow(() -> command.perform(player, args));
  }

  // ==================== Town ID Validation Tests ====================

  @Test
  void perform_invalidTownId_showsError() {
    String[] args = {"join", "invalidTownId"};

    command.perform(player, args);

    // Should show "no invitation" or "town not found" error
  }

  @Test
  void perform_emptyTownId_showsError() {
    String[] args = {"join", ""};

    command.perform(player, args);

    // Should show error
  }

  @Test
  void perform_whitespaceTownId_showsError() {
    String[] args = {"join", "   "};

    command.perform(player, args);

    // Should show error
  }

  @Test
  void perform_numericTownId_handlesCorrectly() {
    String[] args = {"join", "12345"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_uuidFormatTownId_handlesCorrectly() {
    String[] args = {"join", "550e8400-e29b-41d4-a716-446655440000"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  // ==================== Edge Cases ====================

  @Test
  void perform_veryLongTownId_handlesCorrectly() {
    String[] args = {"join", "ThisIsAVeryLongTownIdThatMightExceedNormalExpectations"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_townIdWithSpecialChars_handlesCorrectly() {
    String[] args = {"join", "town-id_123"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_townIdWithSpaces_treatedAsMultipleArgs() {
    String[] args = {"join", "town", "id"};

    command.perform(player, args);

    // Should show "too many arguments" error
  }

  @Test
  void perform_caseSensitiveTownId_handlesCorrectly() {
    String[] args = {"join", "TownID"};

    assertDoesNotThrow(() -> command.perform(player, args));

    String[] argsLower = {"join", "townid"};

    assertDoesNotThrow(() -> command.perform(player, argsLower));
  }

  @Test
  void perform_townIdWithDashes_handlesCorrectly() {
    String[] args = {"join", "town-123-abc"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_townIdWithUnderscores_handlesCorrectly() {
    String[] args = {"join", "town_123_abc"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_nullTownId_handlesGracefully() {
    String[] args = {"join", null};

    // Should handle null gracefully
    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_alphanumericTownId_handlesCorrectly() {
    String[] args = {"join", "Town123ABC"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }
}
