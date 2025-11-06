package org.leralix.tan.dataclass.territory;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

class RegionDataTest {

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
  void testCreation() {

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(server.addPlayer()).join();
    TownData townData = TownDataStorage.getInstance().newTown("testTown", tanPlayer).join();
    RegionData regionData =
        RegionDataStorage.getInstance().createNewRegion("testRegion", townData).join();

    assertSame(tanPlayer, regionData.getLeaderData());
    assertSame(townData, regionData.getCapital());
    assertSame(townData, regionData.getSubjects().getFirst());

    assertEquals(0, regionData.getBalance());
    assertEquals("testRegion", regionData.getName());
    assertEquals(1, regionData.getHierarchyRank());
    assertEquals(1, regionData.getSubjects().size());
    assertEquals(tanPlayer, regionData.getLeaderData());
  }

  @Test
  void testAddVassal() {

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(server.addPlayer()).join();
    TownData townData = TownDataStorage.getInstance().newTown("FirstTown", tanPlayer).join();
    RegionData regionData =
        RegionDataStorage.getInstance().createNewRegion("testRegion", townData).join();
    TownData newTown = TownDataStorage.getInstance().newTown("secondTown").join();

    newTown.setOverlord(regionData);

    assertEquals(2, regionData.getSubjects().size());
    assertTrue(regionData.getSubjects().contains(newTown));
    assertTrue(townData.getOverlord().isPresent());
    assertSame(regionData, townData.getOverlord().get());
    assertTrue(newTown.getOverlord().isPresent());
    assertSame(regionData, newTown.getOverlord().get());
  }

  @Test
  void getAllPlayer() {

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(server.addPlayer()).join();
    ITanPlayer tanPlayer2 = PlayerDataStorage.getInstance().get(server.addPlayer()).join();

    TownData town1 = TownDataStorage.getInstance().newTown("testTown", tanPlayer).join();
    TownData town2 = TownDataStorage.getInstance().newTown("testTown", tanPlayer2).join();

    RegionData regionData =
        RegionDataStorage.getInstance().createNewRegion("testRegion", town1).join();

    town2.setOverlord(regionData);

    assertEquals(2, regionData.getSubjects().size());
    assertTrue(regionData.getITanPlayerList().contains(tanPlayer));
    assertTrue(regionData.getITanPlayerList().contains(tanPlayer2));

    assertNotNull(regionData.getRank(tanPlayer));
    assertNotNull(regionData.getRank(tanPlayer2));
  }

  @Test
  void addPlayerToTownAfterRegionCreation() {

    ITanPlayer leader = PlayerDataStorage.getInstance().get(server.addPlayer()).join();
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(server.addPlayer()).join();

    TownData townData = TownDataStorage.getInstance().newTown("town", leader).join();

    RegionData regionData =
        RegionDataStorage.getInstance().createNewRegion("region", townData).join();

    townData.addPlayer(tanPlayer);

    RankData rankData = tanPlayer.getRegionRank();

    assertNotNull(rankData);
    assertNotNull(regionData.getRank(tanPlayer));
    assertTrue(regionData.getRank(tanPlayer).getPlayers().contains(tanPlayer));

    townData.removePlayer(tanPlayer);

    assertNull(tanPlayer.getRegionRank());
    assertNull(regionData.getRank(tanPlayer));
    assertFalse(rankData.getPlayers().contains(tanPlayer));
    assertNull(tanPlayer.getRegionRankID());
  }
}
