package org.leralix.tan.listeners.chat.events;

import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.territory.RegionData;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.utils.constants.Constants;

import static org.junit.jupiter.api.Assertions.*;

class CreateRegionTest extends BasicTest {

    @Test
    void nominalCase(){
        var tanPlayer = playerDataStorage.get(server.addPlayer());
        var townData = townDataStorage.newTown("Town-B", tanPlayer);
        townData.addToBalance(50);
        String regionName = "Region-B";

        CreateRegion createRegion = new CreateRegion(25);
        createRegion.execute(tanPlayer.getPlayer(), tanPlayer, regionName);

        assertTrue(townData.haveOverlord());
        assertTrue(townData.getRegion().isPresent());
        RegionData regionData = townData.getRegion().get();
        assertFalse(regionData.haveOverlord());
        assertEquals(regionName, regionData.getName());
        assertEquals(1, regionData.getSubjects().size());
        assertEquals(25, townData.getBalance());
        assertEquals(1, regionData.getAllRanks().size());
    }

    @Test
    void playerNotLeader(){
        var tanPlayer = playerDataStorage.get(server.addPlayer());
        var secondTanPlayer = playerDataStorage.get(server.addPlayer());

        var townData = townDataStorage.newTown("Town", tanPlayer);

        townData.addPlayer(secondTanPlayer);

        String regionName = "Region";

        CreateRegion createRegion = new CreateRegion(0);
        createRegion.execute(secondTanPlayer.getPlayer(), tanPlayer, regionName);

        assertFalse(townData.haveOverlord());
    }

    @Test
    void notEnoughMoney(){
        var tanPlayer = playerDataStorage.get(server.addPlayer());

        var townData = townDataStorage.newTown("Town", tanPlayer);

        CreateRegion createRegion = new CreateRegion(1);
        createRegion.execute(tanPlayer.getPlayer(), tanPlayer, "Region");

        assertFalse(townData.haveOverlord());
    }

    @Test
    void regionNameTooLong(){
        var tanPlayer = playerDataStorage.get(server.addPlayer());
        var townData = townDataStorage.newTown("Town", tanPlayer);
        townData.addToBalance(50);

        int maxSize = Constants.getRegionMaxNameSize();

        CreateRegion createRegion = new CreateRegion(25);
        createRegion.execute(tanPlayer.getPlayer(), tanPlayer, "a" + "a".repeat(Math.max(0, maxSize)));

        assertFalse(townData.haveOverlord());
    }

    @Test
    void regionNameAlreadyUsed(){
        var tanPlayer1 = playerDataStorage.get(server.addPlayer());
        var townData1 = townDataStorage.newTown("townData1", tanPlayer1);

        var tanPlayer2 = playerDataStorage.get(server.addPlayer());
        var townData2 = townDataStorage.newTown("townData2", tanPlayer2);

        String regionName = "specificRegionName";

        CreateRegion createRegion = new CreateRegion(0);
        createRegion.execute(tanPlayer1.getPlayer(), tanPlayer1, regionName);
        createRegion.execute(tanPlayer2.getPlayer(), tanPlayer2, regionName);

        assertTrue(townData1.haveOverlord());
        assertFalse(townData2.haveOverlord());
    }

    @Test
    void regionRankAssignedAndRemoved(){
        var tanPlayerA = playerDataStorage.get(server.addPlayer());
        var townDataA = townDataStorage.newTown("Town-A", tanPlayerA);

        var tanPlayerB = playerDataStorage.get(server.addPlayer());
        var townDataB = townDataStorage.newTown("Town-B", tanPlayerB);

        // Create a region, player A gets a rank
        var region = RegionDataStorage.getInstance().createNewRegion("region", townDataA);
        assertEquals(1, region.getAllRanks().size());
        assertEquals(1, region.getDefaultRank().getNumberOfPlayer());
        assertNotNull(tanPlayerA.getRegionRankID());
        assertNull(tanPlayerB.getRegionRankID());

        // Town B joins, player B gets a rank
        townDataB.setOverlord(region);
        assertEquals(1, region.getAllRanks().size());
        assertEquals(2, region.getDefaultRank().getNumberOfPlayer());
        assertNotNull(tanPlayerA.getRegionRankID());
        assertNotNull(tanPlayerB.getRegionRankID());

        // Player B leaves, it loses his region rank
        townDataB.removePlayer(tanPlayerB);
        assertEquals(1, region.getAllRanks().size());
        assertEquals(1, region.getDefaultRank().getNumberOfPlayer());
        assertNotNull(tanPlayerA.getRegionRankID());
        assertNull(tanPlayerB.getRegionRankID());

        // Player B join again his town,
        townDataB.addPlayer(tanPlayerB);
        assertEquals(1, region.getAllRanks().size());
        assertEquals(2, region.getDefaultRank().getNumberOfPlayer());
        assertNotNull(tanPlayerA.getRegionRankID());
        assertNotNull(tanPlayerB.getRegionRankID());

        townDataB.removeOverlord();

        assertEquals(1, region.getAllRanks().size());
        assertEquals(1, region.getDefaultRank().getNumberOfPlayer());
        assertNotNull(tanPlayerA.getRegionRankID());
        assertNull(tanPlayerB.getRegionRankID());
    }
}