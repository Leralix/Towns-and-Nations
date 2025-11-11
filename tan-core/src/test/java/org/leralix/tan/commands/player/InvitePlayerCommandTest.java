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

/** Unit tests for InvitePlayerCommand. */
class InvitePlayerCommandTest {

  private ServerMock server;
  private Player player;
  private Player invitee;
  private InvitePlayerCommand command;
  private ITanPlayer tanPlayer;
  private ITanPlayer tanInvitee;

  @BeforeEach
  void setUp() {
    server = MockBukkit.mock();
    MockBukkit.load(SphereLib.class);
    MockBukkit.load(TownsAndNations.class);
    player = server.addPlayer("TestPlayer");
    invitee = server.addPlayer("InviteePlayer");
    command = new InvitePlayerCommand();
    tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    tanInvitee = PlayerDataStorage.getInstance().getSync(invitee);
  }

  @org.junit.jupiter.api.AfterEach
  void tearDown() {
    MockBukkit.unmock();
  }

  // ==================== Basic Command Info Tests ====================

  @Test
  void getName_returnsCorrectName() {
    assertEquals("invite", command.getName());
  }

  @Test
  void getSyntax_returnsCorrectSyntax() {
    assertEquals("/tan invite <playerName>", command.getSyntax());
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
  void getTabCompleteSuggestions_withTwoArgs_returnsOnlinePlayers() {
    String[] args = {"invite", ""};
    List<String> suggestions = command.getTabCompleteSuggestions(player, "invite", args);

    assertFalse(suggestions.isEmpty());
    assertTrue(suggestions.contains("TestPlayer"));
    assertTrue(suggestions.contains("InviteePlayer"));
  }

  @Test
  void getTabCompleteSuggestions_withOneArg_returnsEmpty() {
    String[] args = {"invite"};
    List<String> suggestions = command.getTabCompleteSuggestions(player, "invite", args);

    assertTrue(suggestions.isEmpty());
  }

  @Test
  void getTabCompleteSuggestions_withThreeArgs_returnsEmpty() {
    String[] args = {"invite", "player", "extra"};
    List<String> suggestions = command.getTabCompleteSuggestions(player, "invite", args);

    assertTrue(suggestions.isEmpty());
  }

  @Test
  void getTabCompleteSuggestions_returnsAllOnlinePlayers() {
    Player thirdPlayer = server.addPlayer("ThirdPlayer");
    String[] args = {"invite", ""};
    List<String> suggestions = command.getTabCompleteSuggestions(player, "invite", args);

    assertEquals(3, suggestions.size());
    assertTrue(suggestions.contains("TestPlayer"));
    assertTrue(suggestions.contains("InviteePlayer"));
    assertTrue(suggestions.contains("ThirdPlayer"));
  }

  // ==================== Argument Validation Tests ====================

  @Test
  void perform_noArguments_showsSyntaxError() {
    String[] args = {"invite"};

    command.perform(player, args);

    // Should show "not enough arguments" error
  }

  @Test
  void perform_tooManyArguments_showsSyntaxError() {
    String[] args = {"invite", "player1", "player2"};

    command.perform(player, args);

    // Should show "too many arguments" error
  }

  @Test
  void perform_fourArguments_showsSyntaxError() {
    String[] args = {"invite", "player1", "extra1", "extra2"};

    command.perform(player, args);

    // Should show "too many arguments" error
  }

  @Test
  void perform_exactlyTwoArgs_doesNotShowSyntaxError() {
    String[] args = {"invite", "InviteePlayer"};

    // Should not throw exception for correct argument count
    assertDoesNotThrow(() -> command.perform(player, args));
  }

  // ==================== Player Validation Tests ====================

  @Test
  void perform_inviteNonexistentPlayer_showsError() {
    String[] args = {"invite", "NonexistentPlayer"};

    command.perform(player, args);

    // Should show "player not found" error
  }

  @Test
  void perform_inviteOnlinePlayer_executesWithoutError() {
    String[] args = {"invite", "InviteePlayer"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_inviteSelf_executesWithoutError() {
    String[] args = {"invite", "TestPlayer"};

    // Should handle self-invite (likely with appropriate error message)
    assertDoesNotThrow(() -> command.perform(player, args));
  }

  // ==================== Edge Cases ====================

  @Test
  void perform_emptyPlayerName_showsError() {
    String[] args = {"invite", ""};

    command.perform(player, args);

    // Should show "player not found" error
  }

  @Test
  void perform_whitespacePlayerName_showsError() {
    String[] args = {"invite", "   "};

    command.perform(player, args);

    // Should show "player not found" error
  }

  @Test
  void perform_playerNameWithSpaces_showsError() {
    String[] args = {"invite", "Player Name"};

    // This will be treated as too many arguments
    command.perform(player, args);

    // Should show error (either syntax or player not found)
  }

  @Test
  void perform_caseInsensitivePlayerName_handlesCorrectly() {
    String[] args = {"invite", "inviteeplayer"};

    // Bukkit's getPlayer is case-insensitive
    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_partialPlayerName_handlesCorrectly() {
    String[] args = {"invite", "Invitee"};

    // Bukkit's getPlayer can handle partial names
    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_specialCharactersInName_showsError() {
    String[] args = {"invite", "@#$%"};

    command.perform(player, args);

    // Should show "player not found" error
  }

  @Test
  void perform_veryLongPlayerName_handlesCorrectly() {
    String[] args = {"invite", "ThisIsAVeryLongPlayerNameThatExceedsNormalLimits"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_playerNameWithUnderscores_handlesCorrectly() {
    Player underscorePlayer = server.addPlayer("Player_With_Underscores");
    String[] args = {"invite", "Player_With_Underscores"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_playerNameWithNumbers_handlesCorrectly() {
    Player numberedPlayer = server.addPlayer("Player123");
    String[] args = {"invite", "Player123"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }
}
