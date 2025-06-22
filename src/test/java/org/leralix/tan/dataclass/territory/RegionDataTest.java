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
        ITanPlayer tanPlayer = AbstractionFactory.getRandomITanPlayer();

        TownData townData = TownDataStorage.getInstance().newTown("testTown", tanPlayer);

        RegionData regionData = RegionDataStorage.getInstance().createNewRegion("testRegion", townData);

        assertSame(tanPlayer, regionData.getLeaderData());
        assertSame(townData, regionData.getCapital());
        assertSame(townData, regionData.getSubjects().get(0));

        assertEquals(0, regionData.getBalance());
        assertEquals("testRegion", regionData.getName());
        assertEquals(1, regionData.getHierarchyRank());
        assertEquals(1, regionData.getSubjects().size());
        assertEquals(tanPlayer, regionData.getLeaderData());
    }

    @Test
    void testAddVassal(){
        ITanPlayer tanPlayer = AbstractionFactory.getRandomITanPlayer();
        TownData townData = TownDataStorage.getInstance().newTown("FirstTown", tanPlayer);
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
        ITanPlayer tanPlayer1 = AbstractionFactory.getRandomITanPlayer();
        ITanPlayer tanPlayer2 = AbstractionFactory.getRandomITanPlayer();

        TownData town1 = TownDataStorage.getInstance().newTown("testTown", tanPlayer1);
        TownData town2 = TownDataStorage.getInstance().newTown("testTown", tanPlayer2);

        RegionData regionData = RegionDataStorage.getInstance().createNewRegion("testRegion", town1);

        town2.setOverlord(regionData);

        assertEquals(2, regionData.getSubjects().size());
        assertTrue(regionData.getITanPlayerList().contains(tanPlayer1));
        assertTrue(regionData.getITanPlayerList().contains(tanPlayer2));

        assertNotNull(regionData.getRank(tanPlayer1));
        assertNotNull(regionData.getRank(tanPlayer2));
    }

    @Test
    void addPlayerToTownAfterRegionCreation(){

        ITanPlayer leader = AbstractionFactory.getRandomITanPlayer();
        TownData townData = TownDataStorage.getInstance().newTown("town", leader);

        RegionData regionData = RegionDataStorage.getInstance().createNewRegion("region", townData);

        ITanPlayer tanPlayer3 = AbstractionFactory.getRandomITanPlayer();

        townData.addPlayer(tanPlayer3);

        RankData rankData = tanPlayer3.getRegionRank();

        assertNotNull(rankData);
        assertNotNull(regionData.getRank(tanPlayer3));
        assertTrue(regionData.getRank(tanPlayer3).getPlayers().contains(tanPlayer3));

        townData.removePlayer(tanPlayer3);

        assertNull(tanPlayer3.getRegionRank());
        assertNull(regionData.getRank(tanPlayer3));
        assertFalse(rankData.getPlayers().contains(tanPlayer3));
        assertNull(tanPlayer3.getRegionRankID());
    }
}