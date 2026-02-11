package org.leralix.tan.utils.territory;

import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.chunk.ClaimedChunk;
import org.leralix.tan.data.chunk.TownClaimedChunk;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;

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
        townData.registerFort(new Vector3D(0, 0, 0, world.getUID().toString()));

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
}