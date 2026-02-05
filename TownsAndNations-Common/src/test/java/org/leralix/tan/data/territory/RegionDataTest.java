package org.leralix.tan.data.territory;

import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.rank.RankData;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.*;

class RegionDataTest extends BasicTest {

    @Test
    void testCreation(){

        ITanPlayer tanPlayer = townsAndNations.getPlayerDataStorage().get(server.addPlayer());
        TownData townData = TownDataStorage.getInstance().newTown("testTown", tanPlayer);
        RegionData regionData = RegionDataStorage.getInstance().createNewRegion("testRegion", townData);

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
    void testAddVassal(){

        ITanPlayer tanPlayer = townsAndNations.getPlayerDataStorage().get(server.addPlayer());
        TownData townData = TownDataStorage.getInstance().newTown("FirstTown", tanPlayer);
        RegionData regionData = RegionDataStorage.getInstance().createNewRegion("testRegion", townData);
        TownData newTown = TownDataStorage.getInstance().newTown("secondTown");

        newTown.setOverlord(regionData);

        assertEquals(2, regionData.getSubjects().size());
        assertTrue(regionData.getSubjects().contains(newTown));
        assertTrue(townData.getOverlordInternal().isPresent());
        assertSame(regionData, townData.getOverlordInternal().get());
        assertTrue(newTown.getOverlordInternal().isPresent());
        assertSame(regionData, newTown.getOverlordInternal().get());
    }

    @Test
    void getAllPlayer(){

        ITanPlayer tanPlayer = townsAndNations.getPlayerDataStorage().get(server.addPlayer());
        ITanPlayer tanPlayer2 = townsAndNations.getPlayerDataStorage().get(server.addPlayer());

        TownData town1 = TownDataStorage.getInstance().newTown("testTown", tanPlayer);
        TownData town2 = TownDataStorage.getInstance().newTown("testTown", tanPlayer2);

        RegionData regionData = RegionDataStorage.getInstance().createNewRegion("testRegion", town1);

        town2.setOverlord(regionData);

        assertEquals(2, regionData.getSubjects().size());
        assertTrue(regionData.getITanPlayerList().contains(tanPlayer));
        assertTrue(regionData.getITanPlayerList().contains(tanPlayer2));

        assertNotNull(regionData.getRank(tanPlayer));
        assertNotNull(regionData.getRank(tanPlayer2));
    }

    @Test
    void addPlayerToTownAfterRegionCreation(){

        ITanPlayer leader = townsAndNations.getPlayerDataStorage().get(server.addPlayer());
        ITanPlayer tanPlayer = townsAndNations.getPlayerDataStorage().get(server.addPlayer());

        TownData townData = TownDataStorage.getInstance().newTown("town", leader);

        RegionData regionData = RegionDataStorage.getInstance().createNewRegion("region", townData);


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