package org.leralix.tan.commands.player;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.enums.ChunkType;
import org.leralix.tan.storage.PlayerAutoClaimStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

/** Unit tests for AutoClaimCommand. */
class AutoClaimCommandTest {

  private ServerMock server;
  private Player player;
  private AutoClaimCommand command;
  private ITanPlayer tanPlayer;

  @BeforeEach
  void setUp() {
    server = MockBukkit.mock();
    MockBukkit.load(SphereLib.class);
    MockBukkit.load(TownsAndNations.class);
    player = server.addPlayer("TestPlayer");
    command = new AutoClaimCommand();
    tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    // Ensure player is not in autoclaim mode before each test
    PlayerAutoClaimStorage.removePlayer(player);
  }

  @org.junit.jupiter.api.AfterEach
  void tearDown() {
    MockBukkit.unmock();
  }

  // ==================== Basic Command Info Tests ====================

  @Test
  void getName_returnsCorrectName() {
    assertEquals("autoclaim", command.getName());
  }

  @Test
  void getSyntax_returnsCorrectSyntax() {
    assertEquals("/tan autoclaim <chunk type>", command.getSyntax());
  }

  @Test
  void getArguments_returnsCorrectCount() {
    assertEquals(1, command.getArguments());
  }

  // ==================== Tab Completion Tests ====================

  @Test
  void getTabCompleteSuggestions_withTwoArgs_returnsSuggestions() {
    String[] args = {"autoclaim", ""};
    List<String> suggestions = command.getTabCompleteSuggestions(player, "autoclaim", args);

    assertFalse(suggestions.isEmpty());
    assertTrue(suggestions.contains("stop"));
  }

  @Test
  void getTabCompleteSuggestions_withOneArg_returnsEmpty() {
    String[] args = {"autoclaim"};
    List<String> suggestions = command.getTabCompleteSuggestions(player, "autoclaim", args);

    assertTrue(suggestions.isEmpty());
  }

  @Test
  void getTabCompleteSuggestions_withThreeArgs_returnsEmpty() {
    String[] args = {"autoclaim", "town", "extra"};
    List<String> suggestions = command.getTabCompleteSuggestions(player, "autoclaim", args);

    assertTrue(suggestions.isEmpty());
  }

  // ==================== Autoclaim Town Tests ====================

  @Test
  void perform_townArgument_enablesTownAutoclaim() {
    String[] args = {"autoclaim", "town"};

    command.perform(player, args);

    assertTrue(PlayerAutoClaimStorage.containsPlayer(player));
    assertEquals(ChunkType.TOWN, PlayerAutoClaimStorage.getChunkType(player));
  }

  // ==================== Autoclaim Region Tests ====================

  @Test
  void perform_regionArgument_enablesRegionAutoclaim() {
    String[] args = {"autoclaim", "region"};

    command.perform(player, args);

    assertTrue(PlayerAutoClaimStorage.containsPlayer(player));
    assertEquals(ChunkType.REGION, PlayerAutoClaimStorage.getChunkType(player));
  }

  // ==================== Stop Autoclaim Tests ====================

  @Test
  void perform_stopArgument_disablesAutoclaim() {
    String[] args = {"autoclaim", "stop"};

    // First enable autoclaim
    PlayerAutoClaimStorage.addPlayer(player, ChunkType.TOWN);
    assertTrue(PlayerAutoClaimStorage.containsPlayer(player));

    // Then stop it
    command.perform(player, args);

    assertFalse(PlayerAutoClaimStorage.containsPlayer(player));
  }

  @Test
  void perform_stopWhenNotEnabled_doesNotThrow() {
    String[] args = {"autoclaim", "stop"};

    assertDoesNotThrow(() -> command.perform(player, args));
    assertFalse(PlayerAutoClaimStorage.containsPlayer(player));
  }

  // ==================== Argument Validation Tests ====================

  @Test
  void perform_noArguments_showsSyntaxError() {
    String[] args = {"autoclaim"};

    command.perform(player, args);

    // Should show syntax error
    assertFalse(PlayerAutoClaimStorage.containsPlayer(player));
  }

  @Test
  void perform_tooManyArguments_showsSyntaxError() {
    String[] args = {"autoclaim", "town", "extra"};

    command.perform(player, args);

    // Should show syntax error
    assertFalse(PlayerAutoClaimStorage.containsPlayer(player));
  }

  @Test
  void perform_invalidArgument_showsSyntaxError() {
    String[] args = {"autoclaim", "invalid"};

    command.perform(player, args);

    // Should show syntax error
    assertFalse(PlayerAutoClaimStorage.containsPlayer(player));
  }

  // ==================== Edge Cases ====================

  @Test
  void perform_uppercaseTown_showsSyntaxError() {
    String[] args = {"autoclaim", "TOWN"};

    command.perform(player, args);

    // Case sensitive - should not enable autoclaim
    assertFalse(PlayerAutoClaimStorage.containsPlayer(player));
  }

  @Test
  void perform_uppercaseRegion_showsSyntaxError() {
    String[] args = {"autoclaim", "REGION"};

    command.perform(player, args);

    // Case sensitive - should not enable autoclaim
    assertFalse(PlayerAutoClaimStorage.containsPlayer(player));
  }

  @Test
  void perform_mixedCaseStop_showsSyntaxError() {
    String[] args = {"autoclaim", "Stop"};

    // Enable autoclaim first
    PlayerAutoClaimStorage.addPlayer(player, ChunkType.TOWN);

    command.perform(player, args);

    // Case sensitive - should not disable autoclaim
    assertTrue(PlayerAutoClaimStorage.containsPlayer(player));
  }

  @Test
  void perform_emptyString_showsSyntaxError() {
    String[] args = {"autoclaim", ""};

    command.perform(player, args);

    assertFalse(PlayerAutoClaimStorage.containsPlayer(player));
  }

  @Test
  void perform_whitespace_showsSyntaxError() {
    String[] args = {"autoclaim", "   "};

    command.perform(player, args);

    assertFalse(PlayerAutoClaimStorage.containsPlayer(player));
  }

  @Test
  void perform_switchBetweenTypes_updatesType() {
    // Enable town autoclaim
    String[] args1 = {"autoclaim", "town"};
    command.perform(player, args1);
    assertEquals(ChunkType.TOWN, PlayerAutoClaimStorage.getChunkType(player));

    // Switch to region autoclaim
    String[] args2 = {"autoclaim", "region"};
    command.perform(player, args2);
    assertEquals(ChunkType.REGION, PlayerAutoClaimStorage.getChunkType(player));
  }
}
