package org.leralix.tan.listeners.chat.events;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

class CreateEmptyTownTest {

  private static Player player;

  @BeforeEach
  void setUp() {
    ServerMock server = MockBukkit.mock();

    MockBukkit.load(SphereLib.class);
    MockBukkit.load(TownsAndNations.class);

    player = server.addPlayer();
  }

  @AfterEach
  public void tearDown() {
    MockBukkit.unmock();
  }

  @Test
  void nominalCase() {
    String townName = "TestTown";
    CreateEmptyTown createEmptyTown = new CreateEmptyTown(null);
    createEmptyTown.execute(player, townName);

    assertTrue(TownDataStorage.getInstance().isNameUsed(townName));
  }
}
