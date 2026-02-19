package org.leralix.tan.utils.territory;

import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.position.Vector2D;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.building.fort.Fort;
import org.leralix.tan.data.chunk.ClaimedChunk;
import org.leralix.tan.data.chunk.TownClaimedChunk;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.RegionData;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.storage.stored.FortStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class ChunkUtilTest extends BasicTest {

    private World world;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        world = server.addSimpleWorld("world");
    }

    @Test
    void testIsChunkEncirecledByValid() {

        ClaimedChunk claimedChunk2 = NewClaimedChunkStorage.getInstance().get(world.getChunkAt(0, 0));

        assertTrue(ChunkUtil.isChunkEncirecledBy(claimedChunk2, claimedChunk -> !claimedChunk.isClaimed()));
    }

    @Test
    void testIsChunkEncirecledByInvalid() {

        TownData townData = townDataStorage.newTown("town");

        NewClaimedChunkStorage newClaimedChunkStorage = NewClaimedChunkStorage.getInstance();

        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(-1, -1), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(-1, 0), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(-1, 1), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(0, -1), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(0, 1), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(1, -1), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(1, 0), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(1, 1), townData.getID());


        ClaimedChunk claimedChunk = NewClaimedChunkStorage.getInstance().get(world.getChunkAt(0, 0));

        assertTrue(ChunkUtil.isChunkEncirecledBy(claimedChunk, ClaimedChunk::isClaimed));
    }


    @Test
    void testGetBorderChunks() {
        TownData townData = townDataStorage.newTown("town");

        NewClaimedChunkStorage newClaimedChunkStorage = NewClaimedChunkStorage.getInstance();

        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(-1, -1), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(-1, 0), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(-1, 1), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(0, -1), townData.getID());
        ClaimedChunk chunkWithoutBorders = newClaimedChunkStorage.claimTownChunk(world.getChunkAt(0, 0), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(0, 1), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(1, -1), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(1, 0), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(1, 1), townData.getID());

        Collection<ClaimedChunk> borderChunks = ChunkUtil.getBorderChunks(townData);

        assertEquals(8, borderChunks.size());
        assertFalse(borderChunks.contains(chunkWithoutBorders));
    }

    @Test
    void test_chunkContainsBuildings_noBuilding() {
        TownData townData = townDataStorage.newTown("town");
        NewClaimedChunkStorage newClaimedChunkStorage = NewClaimedChunkStorage.getInstance();

        TownClaimedChunk chunkFreeFromBuildings = newClaimedChunkStorage.claimTownChunk(world.getChunkAt(0, 0), townData.getID());

        assertFalse(ChunkUtil.chunkContainsBuildings(chunkFreeFromBuildings, townData));
    }

    @Test
    void test_chunkContainsBuildings_withBuilding() {
        TownData townData = townDataStorage.newTown("town");
        NewClaimedChunkStorage newClaimedChunkStorage = NewClaimedChunkStorage.getInstance();

        TownClaimedChunk chunkWithBuilding = newClaimedChunkStorage.claimTownChunk(world.getChunkAt(0, 0), townData.getID());
        Fort fort = FortStorage.getInstance().register(new Vector3D(0, 0, 0, world.getUID().toString()), townData);
        fort.spawnFlag();
        townData.addOwnedFort(fort);


        assertTrue(ChunkUtil.chunkContainsBuildings(chunkWithBuilding, townData));
    }

    @Test
    void test_getChunksInRadius() {
        assertEquals(1, ChunkUtil.getChunksInRadius(world.getChunkAt(0, 0), 0).size());
        assertEquals(5, ChunkUtil.getChunksInRadius(world.getChunkAt(0, 0), 1).size());
        assertEquals(13, ChunkUtil.getChunksInRadius(world.getChunkAt(0, 0), 2).size());
        assertEquals(29, ChunkUtil.getChunksInRadius(world.getChunkAt(0, 0), 3).size());
    }

    @Test
    void test_isChunkInBufferZone() {
        TownData townDataBuffer = townDataStorage.newTown("townBuffer");
        TownData townToClaim = townDataStorage.newTown("townToClaim");

        NewClaimedChunkStorage newClaimedChunkStorage = NewClaimedChunkStorage.getInstance();

        int buffer = 3;

        int x = 0;
        int z = 0;

        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(x, z), townDataBuffer.getID());

        ClaimedChunk chunkInBuffer = newClaimedChunkStorage.get(world.getChunkAt(x + buffer, z));
        ClaimedChunk chunkOutsideBuffer = newClaimedChunkStorage.get(world.getChunkAt(x + buffer + 1, z));


        assertTrue(ChunkUtil.isInBufferZone(chunkInBuffer, townToClaim, buffer));
        assertFalse(ChunkUtil.isInBufferZone(chunkOutsideBuffer, townToClaim, buffer));

    }

    @Test
    void unclaimIfNoLongerSupplied_town(){
        TownData townToClaim = townDataStorage.newTown("town");
        NewClaimedChunkStorage newClaimedChunkStorage = NewClaimedChunkStorage.getInstance();

        var firstChunk = newClaimedChunkStorage.claimTownChunk(world.getChunkAt(0, 0), townToClaim.getID());
        townToClaim.setCapitalLocation(new Vector2D(0, 0, world.getUID().toString()));
        var middleChunk = newClaimedChunkStorage.claimTownChunk(world.getChunkAt(1, 0), townToClaim.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(2, 0), townToClaim.getID());

        newClaimedChunkStorage.unclaimChunkAndUpdate(middleChunk);

        assertEquals(1, newClaimedChunkStorage.getAll().size());
        assertEquals(firstChunk, newClaimedChunkStorage.getAll().values().iterator().next());
    }

    /**
     * Testing that chunk not linked to any vassal claims are unclaimed.
     */
    @Test
    void unclaimIfNoLongerSupplied_region_notLinkedToTown(){
        ITanPlayer player = playerDataStorage.register(server.addPlayer("player"));
        TownData townToClaim = townDataStorage.newTown("town", player);
        RegionData regionData = RegionDataStorage.getInstance().createNewRegion("region", townToClaim);

        NewClaimedChunkStorage newClaimedChunkStorage = NewClaimedChunkStorage.getInstance();

        var firstChunk = newClaimedChunkStorage.claimTownChunk(world.getChunkAt(0, 0), townToClaim.getID());
        townToClaim.setCapitalLocation(new Vector2D(0, 0, world.getUID().toString()));

        var middleChunk = newClaimedChunkStorage.claimRegionChunk(world.getChunkAt(1, 0), regionData.getID());
        newClaimedChunkStorage.claimRegionChunk(world.getChunkAt(2, 0), regionData.getID());

        newClaimedChunkStorage.unclaimChunkAndUpdate(middleChunk);

        assertEquals(1, newClaimedChunkStorage.getAll().size());
        assertEquals(firstChunk, newClaimedChunkStorage.getAll().values().iterator().next());
    }

    /**
     * Testing that chunk not linked to any vassal claims are unclaimed.
     */
    @Test
    void unclaimIfNoLongerSupplied_region_LinkedToTown(){
        ITanPlayer player = playerDataStorage.register(server.addPlayer("player"));
        TownData townToClaim = townDataStorage.newTown("town", player);
        RegionData regionData = RegionDataStorage.getInstance().createNewRegion("region", townToClaim);

        NewClaimedChunkStorage newClaimedChunkStorage = NewClaimedChunkStorage.getInstance();
        townToClaim.setCapitalLocation(new Vector2D(0, 0, world.getUID().toString()));

        var firstChunk = newClaimedChunkStorage.claimTownChunk(world.getChunkAt(0, 0), townToClaim.getID());
        var middleChunk = newClaimedChunkStorage.claimRegionChunk(world.getChunkAt(1, 0), regionData.getID());
        var lastChunk = newClaimedChunkStorage.claimRegionChunk(world.getChunkAt(2, 0), regionData.getID());

        newClaimedChunkStorage.unclaimChunkAndUpdate(lastChunk);

        assertEquals(2, newClaimedChunkStorage.getAll().size());
        assertTrue(newClaimedChunkStorage.getAll().containsValue(firstChunk));
        assertTrue(newClaimedChunkStorage.getAll().containsValue(middleChunk));
    }
}