package org.leralix.tan.utils.territory;

import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.position.Vector2D;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.building.fort.Fort;
import org.leralix.tan.data.chunk.IClaimedChunk;
import org.leralix.tan.data.chunk.TerritoryChunk;
import org.leralix.tan.data.chunk.TownClaimedChunk;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Region;
import org.leralix.tan.data.territory.Town;

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

        IClaimedChunk claimedChunk2 = claimStorage.get(world.getChunkAt(0, 0));

        assertTrue(ChunkUtil.isChunkEncirecledBy(claimedChunk2, claimedChunk -> !claimedChunk.isClaimed()));
    }

    @Test
    void testIsChunkEncirecledByInvalid() {

        Town townData = townStorage.newTown("town");


        claimStorage.claimTownChunk(world.getChunkAt(-1, -1), townData.getID());
        claimStorage.claimTownChunk(world.getChunkAt(-1, 0), townData.getID());
        claimStorage.claimTownChunk(world.getChunkAt(-1, 1), townData.getID());
        claimStorage.claimTownChunk(world.getChunkAt(0, -1), townData.getID());
        claimStorage.claimTownChunk(world.getChunkAt(0, 1), townData.getID());
        claimStorage.claimTownChunk(world.getChunkAt(1, -1), townData.getID());
        claimStorage.claimTownChunk(world.getChunkAt(1, 0), townData.getID());
        claimStorage.claimTownChunk(world.getChunkAt(1, 1), townData.getID());


        IClaimedChunk claimedChunk = claimStorage.get(world.getChunkAt(0, 0));

        assertTrue(ChunkUtil.isChunkEncirecledBy(claimedChunk, IClaimedChunk::isClaimed));
    }


    @Test
    void testGetBorderChunks() {
        Town townData = townStorage.newTown("town");

        claimStorage.claimTownChunk(world.getChunkAt(-1, -1), townData.getID());
        claimStorage.claimTownChunk(world.getChunkAt(-1, 0), townData.getID());
        claimStorage.claimTownChunk(world.getChunkAt(-1, 1), townData.getID());
        claimStorage.claimTownChunk(world.getChunkAt(0, -1), townData.getID());
        IClaimedChunk chunkWithoutBorders = claimStorage.claimTownChunk(world.getChunkAt(0, 0), townData.getID());
        claimStorage.claimTownChunk(world.getChunkAt(0, 1), townData.getID());
        claimStorage.claimTownChunk(world.getChunkAt(1, -1), townData.getID());
        claimStorage.claimTownChunk(world.getChunkAt(1, 0), townData.getID());
        claimStorage.claimTownChunk(world.getChunkAt(1, 1), townData.getID());

        Collection<TerritoryChunk> borderChunks = ChunkUtil.getBorderChunks(townData);

        assertEquals(8, borderChunks.size());
        assertFalse(borderChunks.contains(chunkWithoutBorders));
    }

    @Test
    void test_chunkContainsBuildings_noBuilding() {
        Town townData = townStorage.newTown("town");

        TownClaimedChunk chunkFreeFromBuildings = claimStorage.claimTownChunk(world.getChunkAt(0, 0), townData.getID());

        assertFalse(ChunkUtil.chunkContainsBuildings(chunkFreeFromBuildings, townData));
    }

    @Test
    void test_chunkContainsBuildings_withBuilding() {
        Town townData = townStorage.newTown("town");

        TownClaimedChunk chunkWithBuilding = claimStorage.claimTownChunk(world.getChunkAt(0, 0), townData.getID());
        Fort fort = fortDataStorage.register(new Vector3D(0, 0, 0, world.getUID().toString()), townData);
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
        Town townDataBuffer = townStorage.newTown("townBuffer");
        Town townToClaim = townStorage.newTown("townToClaim");

        int buffer = 3;

        int x = 0;
        int z = 0;

        claimStorage.claimTownChunk(world.getChunkAt(x, z), townDataBuffer.getID());

        IClaimedChunk chunkInBuffer = claimStorage.get(world.getChunkAt(x + buffer, z));
        IClaimedChunk chunkOutsideBuffer = claimStorage.get(world.getChunkAt(x + buffer + 1, z));


        assertTrue(ChunkUtil.isInBufferZone(chunkInBuffer, townToClaim, buffer));
        assertFalse(ChunkUtil.isInBufferZone(chunkOutsideBuffer, townToClaim, buffer));

    }

    @Test
    void unclaimIfNoLongerSupplied_town(){
        Town townToClaim = townStorage.newTown("town");

        var firstChunk = claimStorage.claimTownChunk(world.getChunkAt(0, 0), townToClaim.getID());
        townToClaim.setCapitalLocation(new Vector2D(0, 0, world.getUID().toString()));
        var middleChunk = claimStorage.claimTownChunk(world.getChunkAt(1, 0), townToClaim.getID());
        claimStorage.claimTownChunk(world.getChunkAt(2, 0), townToClaim.getID());

        claimStorage.unclaimChunkAndUpdate(middleChunk);

        assertEquals(1, claimStorage.getAllChunks().size());
        assertEquals(firstChunk, claimStorage.getAllChunks().iterator().next());
    }

    /**
     * Testing that chunk not linked to any vassal claims are unclaimed.
     */
    @Test
    void unclaimIfNoLongerSupplied_region_notLinkedToTown(){
        ITanPlayer player = playerDataStorage.get(server.addPlayer("player"));
        Town townToClaim = townStorage.newTown("town", player);
        Region regionData = regionStorage.newRegion("region", townToClaim);


        var firstChunk = claimStorage.claimTownChunk(world.getChunkAt(0, 0), townToClaim.getID());
        townToClaim.setCapitalLocation(new Vector2D(0, 0, world.getUID().toString()));

        var middleChunk = claimStorage.claimRegionChunk(world.getChunkAt(1, 0), regionData.getID());
        claimStorage.claimRegionChunk(world.getChunkAt(2, 0), regionData.getID());

        claimStorage.unclaimChunkAndUpdate(middleChunk);

        assertEquals(1, claimStorage.getAllChunks().size());
        assertEquals(firstChunk, claimStorage.getAllChunks().iterator().next());
    }

    /**
     * Testing that chunk not linked to any vassal claims are unclaimed.
     */
    @Test
    void unclaimIfNoLongerSupplied_region_LinkedToTown(){
        ITanPlayer player = playerDataStorage.get(server.addPlayer("player"));
        Town townToClaim = townStorage.newTown("town", player);
        Region regionData = regionStorage.newRegion("region", townToClaim);

        townToClaim.setCapitalLocation(new Vector2D(0, 0, world.getUID().toString()));

        var firstChunk = claimStorage.claimTownChunk(world.getChunkAt(0, 0), townToClaim.getID());
        var middleChunk = claimStorage.claimRegionChunk(world.getChunkAt(1, 0), regionData.getID());
        var lastChunk = claimStorage.claimRegionChunk(world.getChunkAt(2, 0), regionData.getID());

        claimStorage.unclaimChunkAndUpdate(lastChunk);

        assertEquals(2, claimStorage.getAllChunks().size());
        assertTrue(claimStorage.getAllChunks().contains(firstChunk));
        assertTrue(claimStorage.getAllChunks().contains(middleChunk));
    }
}