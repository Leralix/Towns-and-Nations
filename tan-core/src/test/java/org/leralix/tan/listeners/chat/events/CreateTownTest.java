package org.leralix.tan.listeners.chat.events;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

class CreateTownTest {

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

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(server.addPlayer()).join();

    CreateTown createTown = new CreateTown(10);
    createTown.execute(tanPlayer.getPlayer(), "town-A");

    assertTrue(tanPlayer.hasTown());
    TownData town = tanPlayer.getTown().join();
    assertEquals(1, town.getAllRanks().size());
    assertEquals(1, town.getTownDefaultRank().getNumberOfPlayer());
    assertEquals(0, town.getBalance());
    assertEquals(1, town.getPlayerIDList().size());
  }

  @Test
  void notEnoughMoney() {

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(server.addPlayer()).join();

    CreateTown createTown = new CreateTown((int) (tanPlayer.getBalance() + 1));
    createTown.execute(tanPlayer.getPlayer(), "anotherName");

    assertFalse(tanPlayer.hasTown());
  }

  @Test
  void nameTooLong() {

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(server.addPlayer()).join();

    int maxSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("RegionNameSize");

    CreateTown createTown = new CreateTown(0);
    createTown.execute(tanPlayer.getPlayer(), "a" + "a".repeat(Math.max(0, maxSize)));

    assertFalse(tanPlayer.hasTown());
  }

  @Test
  void nameAlreadyUsed() {

    ITanPlayer tanPlayer1 = PlayerDataStorage.getInstance().get(server.addPlayer()).join();
    ITanPlayer tanPlayer2 = PlayerDataStorage.getInstance().get(server.addPlayer()).join();

    String townName = "townWithDuplicateName";

    CreateTown createTown = new CreateTown(0);
    createTown.execute(tanPlayer1.getPlayer(), townName);
    createTown.execute(tanPlayer2.getPlayer(), townName);

    assertTrue(tanPlayer1.hasTown());
    assertFalse(tanPlayer2.hasTown());
  }
}
