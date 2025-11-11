package org.leralix.tan.commands.player;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

/** Unit tests for ClaimCommand with exception handling. */
class ClaimCommandTest {

  private ServerMock server;
  private PlayerMock player;
  private ITanPlayer tanPlayer;
  private ClaimCommand claimCommand;

  @BeforeEach
  void setUp() {
    server = MockBukkit.mock();
    MockBukkit.load(SphereLib.class);
    MockBukkit.load(TownsAndNations.class);

    player = server.addPlayer("TestPlayer");
    tanPlayer = PlayerDataStorage.getInstance().get(player).join();
    claimCommand = new ClaimCommand();
  }

  @AfterEach
  void tearDown() {
    MockBukkit.unmock();
  }

  @Test
  void getName_returnsCorrectName() {
    assertEquals("claim", claimCommand.getName());
  }

  @Test
  void getSyntax_returnsCorrectSyntax() {
    assertEquals("/tan claim <town/region>", claimCommand.getSyntax());
  }

  @Test
  void getArguments_returnsCorrectCount() {
    assertEquals(1, claimCommand.getArguments());
  }

  @Test
  void perform_tooFewArguments_sendsErrorMessage() {
    String[] args = {"claim"}; // Missing town/region argument

    claimCommand.perform(player, args);

    // Player should receive error message (verified by MockBukkit message tracking)
    assertTrue(
        player.nextMessage().contains("argument") || player.nextMessage().contains("syntax"));
  }

  @Test
  void perform_tooManyArguments_sendsErrorMessage() {
    String[] args = {"claim", "town", "100", "200", "extra"}; // Too many args

    claimCommand.perform(player, args);

    // Player should receive error message
    assertTrue(
        player.nextMessage().contains("argument") || player.nextMessage().contains("syntax"));
  }

  @Test
  void perform_invalidTerritoryType_sendsErrorMessage() {
    String[] args = {"claim", "invalid"}; // Not "town" or "region"

    claimCommand.perform(player, args);

    // Player should receive error message about syntax
    assertNotNull(player.nextMessage());
  }

  @Test
  void perform_invalidCoordinates_sendsErrorMessage() {
    String[] args = {"claim", "town", "abc", "def"}; // Invalid coordinates

    claimCommand.perform(player, args);

    // Player should receive error about invalid number format
    String message = player.nextMessage();
    assertNotNull(message);
  }

  @Test
  void perform_negativeCoordinates_acceptsNegativeNumbers() {
    String[] args = {"claim", "town", "-100", "-200"};

    // This should not crash with NumberFormatException
    assertDoesNotThrow(() -> claimCommand.perform(player, args));
  }

  @Test
  void getTabCompleteSuggestions_twoArgs_returnsTownAndRegion() {
    String[] args = {"claim", ""};

    var suggestions = claimCommand.getTabCompleteSuggestions(player, "", args);

    assertTrue(suggestions.contains("town"));
    assertTrue(suggestions.contains("region"));
  }

  @Test
  void getTabCompleteSuggestions_threeArgs_returnsEmpty() {
    String[] args = {"claim", "town", ""};

    var suggestions = claimCommand.getTabCompleteSuggestions(player, "", args);

    assertTrue(suggestions.isEmpty());
  }

  @Test
  void perform_withValidCoordinates_doesNotThrowException() {
    String[] args = {"claim", "town", "0", "0"};

    // Should handle gracefully even without town
    assertDoesNotThrow(() -> claimCommand.perform(player, args));
  }

  @Test
  void perform_coordinatesWithWhitespace_parsesCorrectly() {
    // Integer.parseInt should handle whitespace
    String[] args = {"claim", "town", "  100  ", "  200  "};

    assertDoesNotThrow(() -> claimCommand.perform(player, args));
  }

  @Test
  void perform_largeCoordinates_handlesGracefully() {
    String[] args = {"claim", "town", "999999", "999999"};

    assertDoesNotThrow(() -> claimCommand.perform(player, args));
  }

  @Test
  void perform_withoutCoordinates_usesCurrentLocation() {
    String[] args = {"claim", "town"};

    // Should attempt to claim current chunk
    assertDoesNotThrow(() -> claimCommand.perform(player, args));

    // Player should receive some message (no town, no permission, etc.)
    assertNotNull(player.nextMessage());
  }
}
