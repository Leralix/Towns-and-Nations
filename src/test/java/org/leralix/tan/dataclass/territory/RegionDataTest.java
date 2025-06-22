package org.leralix.tan.dataclass.territory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.tan.dataclass.ITanPlayer;
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
        ITanPlayer ITanPlayer = AbstractionFactory.getRandomITanPlayer();

        TownData townData = TownDataStorage.getInstance().newTown("testTown", ITanPlayer);

        RegionData regionData = RegionDataStorage.getInstance().createNewRegion("testRegion", townData);

        assertSame(ITanPlayer, regionData.getLeaderData());
        assertSame(townData, regionData.getCapital());
        assertSame(townData, regionData.getSubjects().get(0));

        assertEquals(0, regionData.getBalance());
        assertEquals("testRegion", regionData.getName());
        assertEquals(1, regionData.getHierarchyRank());
        assertEquals(1, regionData.getSubjects().size());
        assertEquals(ITanPlayer, regionData.getLeaderData());
    }

    @Test
    void testAddVassal(){
        ITanPlayer ITanPlayer = AbstractionFactory.getRandomITanPlayer();
        TownData townData = TownDataStorage.getInstance().newTown("FirstTown", ITanPlayer);
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
        ITanPlayer ITanPlayer1 = AbstractionFactory.getRandomITanPlayer();
        ITanPlayer ITanPlayer2 = AbstractionFactory.getRandomITanPlayer();

        TownData town1 = TownDataStorage.getInstance().newTown("testTown", ITanPlayer1);
        TownData town2 = TownDataStorage.getInstance().newTown("testTown", ITanPlayer2);

        RegionData regionData = RegionDataStorage.getInstance().createNewRegion("testRegion", town1);

        town2.setOverlord(regionData);

        assertEquals(2, regionData.getSubjects().size());
        assertTrue(regionData.getITanPlayerList().contains(ITanPlayer1));
        assertTrue(regionData.getITanPlayerList().contains(ITanPlayer2));

        assertNotNull(regionData.getRank(ITanPlayer1));
        assertNotNull(regionData.getRank(ITanPlayer2));
    }

    @Test
    void addPlayerToTownAfterRegionCreation(){

        ITanPlayer leader = AbstractionFactory.getRandomITanPlayer();
        TownData townData = TownDataStorage.getInstance().newTown("town", leader);

        RegionData regionData = RegionDataStorage.getInstance().createNewRegion("region", townData);

        ITanPlayer ITanPlayer3 = AbstractionFactory.getRandomITanPlayer();

        townData.addPlayer(ITanPlayer3);

        RankData rankData = ITanPlayer3.getRegionRank();

        assertNotNull(rankData);
        assertNotNull(regionData.getRank(ITanPlayer3));
        assertTrue(regionData.getRank(ITanPlayer3).getPlayers().contains(ITanPlayer3));

        townData.removePlayer(ITanPlayer3);

        assertNull(ITanPlayer3.getRegionRank());
        assertNull(regionData.getRank(ITanPlayer3));
        assertFalse(rankData.getPlayers().contains(ITanPlayer3));
        assertNull(ITanPlayer3.getRegionRankID());
    }
}