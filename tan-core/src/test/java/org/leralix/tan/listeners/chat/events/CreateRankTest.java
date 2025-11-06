package org.leralix.tan.listeners.chat.events;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

class CreateRankTest {

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
  void nominalCase() {
    var tanPlayer = PlayerDataStorage.getInstance().get(server.addPlayer()).join();
    TownData townData = TownDataStorage.getInstance().newTown("TestTown", tanPlayer).join();

    assertEquals(1, townData.getAllRanks().size());

    CreateRank createRank = new CreateRank(townData, null);
    boolean result = createRank.execute(tanPlayer.getPlayer(), "TestRank");
    assertEquals(true, result);
    assertEquals(2, townData.getAllRanks().size());
  }

  @Test
  void duplicateNameAllowed() {
    var tanPlayer = PlayerDataStorage.getInstance().get(server.addPlayer()).join();
    TownData townData = TownDataStorage.getInstance().newTown("TestTown", tanPlayer).join();
    String newRankName = "TestRank";

    assertEquals(1, townData.getAllRanks().size());
    CreateRank createRank = new CreateRank(townData, null);
    boolean result1 = createRank.execute(tanPlayer.getPlayer(), newRankName);
    assertEquals(true, result1);
    assertEquals(2, townData.getAllRanks().size());
    boolean result2 = createRank.execute(tanPlayer.getPlayer(), newRankName);
    assertEquals(false, result2);
    assertEquals(2, townData.getAllRanks().size());
  }
}
