package org.leralix.tan.listeners.chat.events;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

class CreateRegionTest {

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
    var townData = TownDataStorage.getInstance().newTown("Town-B", tanPlayer).join();
    townData.addToBalance(50);
    String regionName = "Region-B";

    CreateRegion createRegion = new CreateRegion(25);
    boolean result = createRegion.execute(tanPlayer.getPlayer(), regionName);

    assertTrue(result);
    assertTrue(townData.haveOverlord());
    RegionData regionData = (RegionData) townData.getOverlord().orElseThrow();
    assertFalse(regionData.haveOverlord());
    assertEquals(regionName, regionData.getName());
    assertEquals(1, regionData.getSubjects().size());
    assertEquals(25, townData.getBalance());
  }

  @Test
  void playerNotLeader() {
    var tanPlayer = PlayerDataStorage.getInstance().get(server.addPlayer()).join();
    var secondTanPlayer = PlayerDataStorage.getInstance().get(server.addPlayer()).join();

    var townData = TownDataStorage.getInstance().newTown("Town", tanPlayer).join();

    townData.addPlayer(secondTanPlayer);

    String regionName = "Region";

    CreateRegion createRegion = new CreateRegion(0);
    boolean result = createRegion.execute(secondTanPlayer.getPlayer(), regionName);

    assertFalse(result);
    assertFalse(townData.haveOverlord());
  }

  @Test
  void notEnoughMoney() {
    var tanPlayer = PlayerDataStorage.getInstance().get(server.addPlayer()).join();

    var townData = TownDataStorage.getInstance().newTown("Town", tanPlayer).join();

    CreateRegion createRegion = new CreateRegion(1);
    boolean result = createRegion.execute(tanPlayer.getPlayer(), "Region");

    assertFalse(result);
    assertFalse(townData.haveOverlord());
  }

  @Test
  void regionNameTooLong() {
    var tanPlayer = PlayerDataStorage.getInstance().get(server.addPlayer()).join();
    var townData = TownDataStorage.getInstance().newTown("Town", tanPlayer).join();
    townData.addToBalance(50);

    int maxSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("RegionNameSize");

    CreateRegion createRegion = new CreateRegion(25);
    boolean result =
        createRegion.execute(tanPlayer.getPlayer(), "a" + "a".repeat(Math.max(0, maxSize)));

    assertFalse(result);
    assertFalse(townData.haveOverlord());
  }

  @Test
  void regionNameAlreadyUsed() {
    var tanPlayer1 = PlayerDataStorage.getInstance().get(server.addPlayer()).join();
    var townData1 = TownDataStorage.getInstance().newTown("townData1", tanPlayer1).join();

    var tanPlayer2 = PlayerDataStorage.getInstance().get(server.addPlayer()).join();
    var townData2 = TownDataStorage.getInstance().newTown("townData2", tanPlayer2).join();

    String regionName = "specificRegionName";

    CreateRegion createRegion = new CreateRegion(0);
    boolean result1 = createRegion.execute(tanPlayer1.getPlayer(), regionName);
    boolean result2 = createRegion.execute(tanPlayer2.getPlayer(), regionName);

    assertTrue(result1);
    assertFalse(result2);
    assertTrue(townData1.haveOverlord());
    assertFalse(townData2.haveOverlord());
  }
}
