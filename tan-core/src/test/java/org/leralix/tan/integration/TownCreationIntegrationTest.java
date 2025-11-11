package org.leralix.tan.integration;

import static org.junit.jupiter.api.Assertions.*;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

/**
 * Integration test for town creation workflow.
 *
 * <p>Tests the complete flow of creating a town, from player registration to town storage.
 *
 * @since 0.16.0
 */
@Disabled("MockBukkit initialization issues - Enable after fixing")
public class TownCreationIntegrationTest extends IntegrationTestBase {

  @Test
  void testCreateTownFlow() {
    // Arrange
    PlayerMock player = createPlayer("TestPlayer");
    String townName = "TestTown";

    // Act
    TownData town = createTown(townName, player);

    // Assert - Town created correctly
    assertNotNull(town, "Town should be created");
    assertEquals(townName, town.getName(), "Town name should match");
    assertEquals(player.getUniqueId().toString(), town.getLeaderID(), "Leader should be set");

    // Assert - Town stored correctly
    TownData retrieved = TownDataStorage.getInstance().getSync(town.getID());
    assertNotNull(retrieved, "Town should be stored");
    assertEquals(townName, retrieved.getName(), "Stored town name should match");

    // Assert - Player registered in town
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    assertEquals(town.getID(), tanPlayer.getTownID(), "Player should be in the town");
  }

  @Test
  void testCreateMultipleTowns() {
    // Arrange & Act
    Object[] player1Data = createPlayerWithTown("Player1", "Town1");
    Object[] player2Data = createPlayerWithTown("Player2", "Town2");

    PlayerMock player1 = (PlayerMock) player1Data[0];
    TownData town1 = (TownData) player1Data[1];

    PlayerMock player2 = (PlayerMock) player2Data[0];
    TownData town2 = (TownData) player2Data[1];

    // Assert - Both towns exist
    assertNotEquals(town1.getID(), town2.getID(), "Towns should have different IDs");
    assertEquals(2, TownDataStorage.getInstance().size(), "Should have 2 towns");

    // Assert - Players are in correct towns
    ITanPlayer tanPlayer1 = PlayerDataStorage.getInstance().getSync(player1);
    ITanPlayer tanPlayer2 = PlayerDataStorage.getInstance().getSync(player2);

    assertEquals(town1.getID(), tanPlayer1.getTownID(), "Player1 should be in Town1");
    assertEquals(town2.getID(), tanPlayer2.getTownID(), "Player2 should be in Town2");
  }

  @Test
  void testTownEconomyInitialization() {
    // Arrange & Act
    Object[] data = createPlayerWithTown("Leader", "EconomyTown");
    TownData town = (TownData) data[1];

    // Assert - Economy initialized correctly
    assertEquals(0.0, town.getBalance(), "Initial balance should be 0");
    assertEquals(1.0, town.getBaseTax(), "Base tax should be 1.0");

    // Act - Add money to treasury
    town.addToBalance(1000.0);

    // Assert - Balance updated
    assertEquals(1000.0, town.getBalance(), "Balance should be updated");

    // Act - Remove money
    boolean success = town.removeFromBalance(500.0);

    // Assert - Withdrawal successful
    assertTrue(success, "Withdrawal should succeed");
    assertEquals(500.0, town.getBalance(), "Balance should be reduced");
  }

  @Test
  void testTownChunksInitialization() {
    // Arrange & Act
    Object[] data = createPlayerWithTown("Leader", "ChunkTown");
    TownData town = (TownData) data[1];

    // Assert - Chunks initialized
    assertNotNull(town.getChunks(), "Chunks component should be initialized");
    assertEquals(0, town.getNumberOfClaimedChunk(), "No chunks claimed initially");

    // Act - Add available claims
    town.getChunks().addAvailableClaims("world", 10);

    // Assert - Claims added
    assertEquals(10, town.getChunks().getAvailableClaims("world"), "Should have 10 claims");
    assertTrue(
        town.getChunks().canClaimInWorld("world"), "Should be able to claim in world");
  }

  @Test
  void testTownDeletion() {
    // Arrange
    Object[] data = createPlayerWithTown("Leader", "DeleteMe");
    PlayerMock player = (PlayerMock) data[0];
    TownData town = (TownData) data[1];

    String townId = town.getID();

    // Act - Delete town
    TownDataStorage.getInstance().delete(townId);
    waitForAsync(); // Wait for async deletion

    // Assert - Town deleted
    assertNull(
        TownDataStorage.getInstance().getSync(townId), "Town should be deleted");

    // Assert - Player no longer in town
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    assertNull(tanPlayer.getTownID(), "Player should not be in a town");
  }
}
