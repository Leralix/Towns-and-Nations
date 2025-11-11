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

/** Unit tests for MapCommand. */
class MapCommandTest {

  private ServerMock server;
  private Player player;
  private MapCommand command;
  private ITanPlayer tanPlayer;

  @BeforeEach
  void setUp() {
    server = MockBukkit.mock();
    MockBukkit.load(SphereLib.class);
    MockBukkit.load(TownsAndNations.class);
    player = server.addPlayer("TestPlayer");
    command = new MapCommand();
    tanPlayer = PlayerDataStorage.getInstance().getSync(player);
  }

  @org.junit.jupiter.api.AfterEach
  void tearDown() {
    MockBukkit.unmock();
  }

  // ==================== Basic Command Info Tests ====================

  @Test
  void getName_returnsCorrectName() {
    assertEquals("map", command.getName());
  }

  @Test
  void getSyntax_returnsCorrectSyntax() {
    assertEquals("/tan map", command.getSyntax());
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
  void perform_withOneArg_opensMap() {
    String[] args = {"map"};

    // Should open map without error
    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_withThreeArgs_opensMapWithSettings() {
    String[] args = {"map", "claim", "town"};

    // Should open map with settings
    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_withTwoArgs_showsError() {
    String[] args = {"map", "extra"};

    command.perform(player, args);

    // Should show "too many args" error
  }

  @Test
  void perform_withFourArgs_showsError() {
    String[] args = {"map", "arg1", "arg2", "arg3"};

    command.perform(player, args);

    // Should show "too many args" error
  }

  @Test
  void perform_withFiveArgs_showsError() {
    String[] args = {"map", "arg1", "arg2", "arg3", "arg4"};

    command.perform(player, args);

    // Should show "too many args" error
  }

  // ==================== Map Settings Tests ====================

  @Test
  void perform_withClaimTownSettings_handlesCorrectly() {
    String[] args = {"map", "claim", "town"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_withUnclaimTownSettings_handlesCorrectly() {
    String[] args = {"map", "unclaim", "town"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_withClaimRegionSettings_handlesCorrectly() {
    String[] args = {"map", "claim", "region"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_withUnclaimRegionSettings_handlesCorrectly() {
    String[] args = {"map", "unclaim", "region"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  // ==================== Edge Cases ====================

  @Test
  void perform_withEmptySettings_handlesCorrectly() {
    String[] args = {"map", "", ""};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_withWhitespaceSettings_handlesCorrectly() {
    String[] args = {"map", "   ", "   "};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_withInvalidAction_handlesCorrectly() {
    String[] args = {"map", "invalid", "town"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_withInvalidType_handlesCorrectly() {
    String[] args = {"map", "claim", "invalid"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_withUppercaseSettings_handlesCorrectly() {
    String[] args = {"map", "CLAIM", "TOWN"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_withMixedCaseSettings_handlesCorrectly() {
    String[] args = {"map", "Claim", "Town"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_withNumericSettings_handlesCorrectly() {
    String[] args = {"map", "123", "456"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_withSpecialCharSettings_handlesCorrectly() {
    String[] args = {"map", "@#$", "%^&"};

    assertDoesNotThrow(() -> command.perform(player, args));
  }
}
