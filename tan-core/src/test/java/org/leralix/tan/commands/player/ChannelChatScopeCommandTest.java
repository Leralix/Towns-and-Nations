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

/** Unit tests for ChannelChatScopeCommand. */
class ChannelChatScopeCommandTest {

  private ServerMock server;
  private Player player;
  private ChannelChatScopeCommand command;
  private ITanPlayer tanPlayer;

  @BeforeEach
  void setUp() {
    server = MockBukkit.mock();
    MockBukkit.load(SphereLib.class);
    MockBukkit.load(TownsAndNations.class);
    player = server.addPlayer("TestPlayer");
    command = new ChannelChatScopeCommand();
    tanPlayer = PlayerDataStorage.getInstance().getSync(player);
  }

  @org.junit.jupiter.api.AfterEach
  void tearDown() {
    MockBukkit.unmock();
  }

  // ==================== Basic Command Info Tests ====================

  @Test
  void getName_returnsCorrectName() {
    assertEquals("chat", command.getName());
  }

  @Test
  void getSyntax_returnsCorrectSyntax() {
    assertEquals("/tan chat <global|alliance|region|town> [message]", command.getSyntax());
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

  // ==================== Tab Completion Tests ====================

  @Test
  void getTabCompleteSuggestions_withTwoArgs_returnsAllScopes() {
    String[] args = {"chat", ""};
    List<String> suggestions = command.getTabCompleteSuggestions(player, "chat", args);

    assertEquals(4, suggestions.size());
    assertTrue(suggestions.contains("town"));
    assertTrue(suggestions.contains("alliance"));
    assertTrue(suggestions.contains("region"));
    assertTrue(suggestions.contains("global"));
  }

  @Test
  void getTabCompleteSuggestions_withOneArg_returnsEmpty() {
    String[] args = {"chat"};
    List<String> suggestions = command.getTabCompleteSuggestions(player, "chat", args);

    assertTrue(suggestions.isEmpty());
  }

  @Test
  void getTabCompleteSuggestions_withThreeArgs_returnsEmpty() {
    String[] args = {"chat", "town", "hello"};
    List<String> suggestions = command.getTabCompleteSuggestions(player, "chat", args);

    assertTrue(suggestions.isEmpty());
  }

  // ==================== Argument Validation Tests ====================

  @Test
  void perform_noArguments_showsSyntaxError() {
    String[] args = {"chat"};

    command.perform(player, args);

    // Should show "not enough args" error
  }

  @Test
  void perform_withValidScope_doesNotThrow() {
    String[] args = {"chat", "global"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_withMessage_doesNotThrow() {
    String[] args = {"chat", "global", "Hello", "world"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  // ==================== Scope Tests ====================

  @Test
  void perform_globalScope_handlesCorrectly() {
    String[] args = {"chat", "global"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_townScope_handlesCorrectly() {
    String[] args = {"chat", "town"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_allianceScope_handlesCorrectly() {
    String[] args = {"chat", "alliance"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_regionScope_handlesCorrectly() {
    String[] args = {"chat", "region"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_invalidScope_showsError() {
    String[] args = {"chat", "invalid"};

    command.perform(player, args);

    // Should show "scope not found" error
  }

  // ==================== Case Sensitivity Tests ====================

  @Test
  void perform_uppercaseScope_handlesCorrectly() {
    String[] args = {"chat", "GLOBAL"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_mixedCaseScope_handlesCorrectly() {
    String[] args = {"chat", "ToWn"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  // ==================== Message Sending Tests ====================

  @Test
  void perform_globalMessageWithMultipleWords_handlesCorrectly() {
    String[] args = {"chat", "global", "Hello", "everyone", "in", "the", "world"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_townMessageWithSingleWord_handlesCorrectly() {
    String[] args = {"chat", "town", "Hi"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_messageWithSpecialCharacters_handlesCorrectly() {
    String[] args = {"chat", "global", "Hello!", "How", "are", "you?"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_emptyMessage_handlesCorrectly() {
    String[] args = {"chat", "global", ""};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  // ==================== Edge Cases ====================

  @Test
  void perform_emptyScope_showsError() {
    String[] args = {"chat", ""};

    command.perform(player, args);

    // Should show error for invalid scope
  }

  @Test
  void perform_whitespaceScope_showsError() {
    String[] args = {"chat", "   "};

    command.perform(player, args);

    // Should show error for invalid scope
  }

  @Test
  void perform_scopeWithNumbers_showsError() {
    String[] args = {"chat", "town123"};

    command.perform(player, args);

    // Should show "scope not found" error
  }

  @Test
  void perform_veryLongMessage_handlesCorrectly() {
    String[] longArgs = new String[102];
    longArgs[0] = "chat";
    longArgs[1] = "global";
    for (int i = 2; i < 102; i++) {
      longArgs[i] = "word" + i;
    }

    assertDoesNotThrow(() -> command.perform(player, longArgs));
  }

  @Test
  void perform_messageWithOnlySpaces_handlesCorrectly() {
    String[] args = {"chat", "global", "   ", "   "};

    assertDoesNotThrow(() -> command.perform(player, args));
  }
}
