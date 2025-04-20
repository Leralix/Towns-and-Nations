package org.leralix.tan.dataclass.territory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.factory.AbstractionFactory;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.*;

class RegionDataTest {

    @BeforeAll
    static void setUp() {
        AbstractionFactory.initializeConfigs();
    }

    @Test
    void testCreation(){
        PlayerData playerData = AbstractionFactory.getRandomPlayerData();

        TownData townData = TownDataStorage.getInstance().newTown("testTown", playerData);

        RegionData regionData = RegionDataStorage.getInstance().createNewRegion("testRegion", townData);

        assertSame(playerData, regionData.getLeaderData());
        assertSame(townData, regionData.getCapital());
        assertSame(townData, regionData.getSubjects().get(0));

        assertEquals(0, regionData.getBalance());
        assertEquals("testRegion", regionData.getName());
        assertEquals(1, regionData.getHierarchyRank());
        assertEquals(1, regionData.getSubjects().size());
        assertEquals(playerData, regionData.getLeaderData());
    }

    @Test
    void testAddVassal(){
        PlayerData playerData = AbstractionFactory.getRandomPlayerData();
        TownData townData = TownDataStorage.getInstance().newTown("FirstTown", playerData);
        RegionData regionData = RegionDataStorage.getInstance().createNewRegion("testRegion", townData);
        TownData newTown = TownDataStorage.getInstance().newTown("secondTown");

        newTown.setOverlord(regionData);

        assertEquals(2, regionData.getSubjects().size());
        assertTrue(regionData.getSubjects().contains(newTown));
        assertSame(regionData, townData.getOverlord());
        assertSame(regionData, newTown.getOverlord());
    }

    @Test
    void getAllPlayer(){
        PlayerData playerData1 = AbstractionFactory.getRandomPlayerData();
        PlayerData playerData2 = AbstractionFactory.getRandomPlayerData();

        TownData town1 = TownDataStorage.getInstance().newTown("testTown", playerData1);
        TownData town2 = TownDataStorage.getInstance().newTown("testTown", playerData2);

        RegionData regionData = RegionDataStorage.getInstance().createNewRegion("testRegion", town1);

        town2.setOverlord(regionData);

        assertEquals(2, regionData.getSubjects().size());
        assertTrue(regionData.getPlayerDataList().contains(playerData1));
        assertTrue(regionData.getPlayerDataList().contains(playerData2));

        assertNotNull(regionData.getRank(playerData1));
        assertNotNull(regionData.getRank(playerData2));
    }

    @Test
    void addPlayerToTownAfterRegionCreation(){

        PlayerData leader = AbstractionFactory.getRandomPlayerData();
        TownData townData = TownDataStorage.getInstance().newTown("town", leader);

        RegionData regionData = RegionDataStorage.getInstance().createNewRegion("region", townData);

        PlayerData playerData3 = AbstractionFactory.getRandomPlayerData();

        townData.addPlayer(playerData3);

        RankData rankData = playerData3.getRegionRank();

        assertNotNull(rankData);
        assertNotNull(regionData.getRank(playerData3));
        assertTrue(regionData.getRank(playerData3).getPlayers().contains(playerData3));

        townData.removePlayer(playerData3);

        assertNull(playerData3.getRegionRank());
        assertNull(regionData.getRank(playerData3));
        assertFalse(rankData.getPlayers().contains(playerData3));
        assertNull(playerData3.getRegionRankID());
    }
}