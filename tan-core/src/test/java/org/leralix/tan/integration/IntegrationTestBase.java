package org.leralix.tan.integration;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

/**
 * Base class for integration tests.
 *
 * <p>Integration tests verify that multiple components work together correctly. Unlike unit tests,
 * they test end-to-end workflows.
 *
 * <p>This class provides:
 *
 * <ul>
 *   <li>MockBukkit server setup
 *   <li>Plugin initialization
 *   <li>Helper methods for creating test data
 *   <li>Automatic cleanup after tests
 * </ul>
 *
 * @since 0.16.0
 */
public abstract class IntegrationTestBase {

  protected ServerMock server;
  protected TownsAndNations plugin;

  @BeforeEach
  public void setUp() {
    // Initialize MockBukkit server
    server = MockBukkit.mock();

    // Load plugin
    plugin = MockBukkit.load(TownsAndNations.class);

    // Additional setup
    onSetUp();
  }

  @AfterEach
  public void tearDown() {
    // Clean up
    onTearDown();

    // Unload plugin and server
    MockBukkit.unmock();
  }

  /**
   * Override this method to add custom setup logic.
   *
   * <p>Called after MockBukkit and plugin are initialized.
   */
  protected void onSetUp() {
    // Override in subclasses if needed
  }

  /**
   * Override this method to add custom cleanup logic.
   *
   * <p>Called before MockBukkit shutdown.
   */
  protected void onTearDown() {
    // Override in subclasses if needed
  }

  // ===== Helper Methods =====

  /**
   * Creates a mock player and registers them in the plugin.
   *
   * @param name Player name
   * @return Mock player
   */
  protected PlayerMock createPlayer(String name) {
    PlayerMock player = server.addPlayer(name);

    // Register player in storage
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);

    return player;
  }

  /**
   * Creates a town with a leader.
   *
   * @param townName Town name
   * @param leader Leader player
   * @return Created town
   */
  protected TownData createTown(String townName, PlayerMock leader) {
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(leader);

    TownData town = new TownData("town-" + townName, townName, tanPlayer);

    // Save to storage
    TownDataStorage.getInstance().save(town);

    return town;
  }

  /**
   * Creates a player and makes them the leader of a new town.
   *
   * @param playerName Player name
   * @param townName Town name
   * @return Array [PlayerMock, TownData]
   */
  protected Object[] createPlayerWithTown(String playerName, String townName) {
    PlayerMock player = createPlayer(playerName);
    TownData town = createTown(townName, player);
    return new Object[] {player, town};
  }

  /**
   * Waits for async operations to complete.
   *
   * @param maxWaitMillis Maximum time to wait in milliseconds
   */
  protected void waitForAsync(long maxWaitMillis) {
    try {
      Thread.sleep(maxWaitMillis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Waits for async operations with default timeout of 1 second.
   */
  protected void waitForAsync() {
    waitForAsync(1000);
  }
}
