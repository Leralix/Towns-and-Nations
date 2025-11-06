package org.leralix.tan.storage.stored;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

class PlayerDataStorageTest {

  private ServerMock server;

  @BeforeEach
  void setUp() {
    server = MockBukkit.mock();

    MockBukkit.load(SphereLib.class);
    MockBukkit.load(TownsAndNations.class);
  }

  @AfterEach
  public void tearDown() {
    MockBukkit.unmock();
  }

  @Test
  void testGetInstance() {
    PlayerDataStorage storage = PlayerDataStorage.getInstance();
    assertNotNull(storage, "PlayerDataStorage instance should not be null");
  }

  @Test
  void testCreatePlayerData() {
    PlayerDataStorage storage = PlayerDataStorage.getInstance();

    PlayerMock player = server.addPlayer("TestPlayer");

    // Create player data
    ITanPlayer tanPlayer = storage.get(player).join();

    // Verify that the player data was created
    assertNotNull(tanPlayer, "Player data should be created and retrievable");
  }
}
