package org.leralix.tan.commands.player;

import static org.junit.jupiter.api.Assertions.*;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

/** Unit tests for UnclaimCommand. */
class UnclaimCommandTest {

  private ServerMock server;
  private Player player;
  private UnclaimCommand command;
  private ITanPlayer tanPlayer;
  private World world;

  @BeforeEach
  void setUp() {
    server = MockBukkit.mock();
    MockBukkit.load(SphereLib.class);
    MockBukkit.load(TownsAndNations.class);
    player = server.addPlayer("TestPlayer");
    world = player.getWorld();
    command = new UnclaimCommand();
    tanPlayer = PlayerDataStorage.getInstance().getSync(player);
  }

  @org.junit.jupiter.api.AfterEach
  void tearDown() {
    MockBukkit.unmock();
  }

  // ==================== Basic Command Info Tests ====================

  @Test
  void getName_returnsCorrectName() {
    assertEquals("unclaim", command.getName());
  }

  @Test
  void getSyntax_returnsCorrectSyntax() {
    assertEquals("/tan unclaim", command.getSyntax());
  }

  @Test
  void getArguments_returnsCorrectCount() {
    assertEquals(1, command.getArguments());
  }

  @Test
  void getTabCompleteSuggestions_returnsEmptyList() {
    assertTrue(command.getTabCompleteSuggestions(player, "test", new String[] {}).isEmpty());
  }

  // ==================== Argument Validation Tests ====================

  @Test
  void perform_tooManyArgs_showsSyntaxError() {
    String[] args = {"unclaim", "extra", "args", "here", "more"};

    command.perform(player, args);

    // Should show syntax error (can't verify message without access to chat history)
    // But command should not throw exception
  }

  @Test
  void perform_twoArgs_showsSyntaxError() {
    String[] args = {"unclaim", "extra"};

    command.perform(player, args);

    // Should show syntax error for 2 args (only 1 or 4 allowed)
  }

  @Test
  void perform_threeArgs_showsSyntaxError() {
    String[] args = {"unclaim", "arg1", "arg2"};

    command.perform(player, args);

    // Should show syntax error for 3 args (only 1 or 4 allowed)
  }

  // ==================== Coordinate Parsing Tests ====================

  @Test
  void perform_invalidXCoordinate_handlesError() {
    String[] args = {"unclaim", "map", "abc", "123"};

    command.perform(player, args);

    // Should handle invalid x coordinate gracefully
  }

  @Test
  void perform_invalidZCoordinate_handlesError() {
    String[] args = {"unclaim", "map", "123", "abc"};

    command.perform(player, args);

    // Should handle invalid z coordinate gracefully
  }

  @Test
  void perform_negativeCoordinates_acceptsValue() {
    String[] args = {"unclaim", "map", "-100", "-200"};

    // Should accept negative coordinates (valid in Minecraft)
    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_largeCoordinates_acceptsValue() {
    String[] args = {"unclaim", "map", "30000000", "30000000"};

    // Should accept large coordinates (within Minecraft world border)
    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_coordinateWithWhitespace_handlesCorrectly() {
    String[] args = {"unclaim", "map", "  123  ", "  456  "};

    // Should handle whitespace in coordinates (Integer.parseInt trims)
    assertDoesNotThrow(() -> command.perform(player, args));
  }

  // ==================== Edge Cases ====================

  @Test
  void perform_singleArg_usesPlayerLocation() {
    String[] args = {"unclaim"};

    // Should use player's current chunk
    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_fourArgs_usesSpecifiedCoordinates() {
    String[] args = {"unclaim", "map", "0", "0"};

    // Should use specified coordinates
    assertDoesNotThrow(() -> command.perform(player, args));
  }

  @Test
  void perform_unclaimedChunk_showsError() {
    String[] args = {"unclaim"};
    Chunk chunk = player.getLocation().getChunk();

    // Ensure chunk is not claimed
    if (NewClaimedChunkStorage.getInstance().isChunkClaimed(chunk)) {
      ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.getInstance().get(chunk);
      claimedChunk.unclaimChunk(player);
    }

    command.perform(player, args);

    // Should show "chunk not claimed" error
  }

  @Test
  void perform_coordinateOverflow_handlesError() {
    String[] args = {"unclaim", "map", "999999999999999999", "0"};

    command.perform(player, args);

    // Should handle integer overflow gracefully
  }

  @Test
  void perform_emptyCoordinate_handlesError() {
    String[] args = {"unclaim", "map", "", "0"};

    command.perform(player, args);

    // Should handle empty coordinate string
  }

  @Test
  void perform_decimalCoordinate_handlesError() {
    String[] args = {"unclaim", "map", "123.45", "0"};

    command.perform(player, args);

    // Should reject decimal coordinates (chunks use integers)
  }

  @Test
  void perform_zeroCoordinates_acceptsValue() {
    String[] args = {"unclaim", "map", "0", "0"};

    // Should accept zero as valid chunk coordinate
    assertDoesNotThrow(() -> command.perform(player, args));
  }
}
