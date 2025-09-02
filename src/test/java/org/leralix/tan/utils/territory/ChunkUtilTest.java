package org.leralix.tan.utils.territory;

import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.BasicTest;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TownClaimedChunk;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

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

        ClaimedChunk2 claimedChunk2 = NewClaimedChunkStorage.getInstance().get(world.getChunkAt(0,0));

        assertTrue(ChunkUtil.isChunkEncirecledBy(claimedChunk2, claimedChunk -> !claimedChunk.isClaimed()));
    }

    @Test
    void testIsChunkEncirecledByInvalid() {

        TownData townData = TownDataStorage.getInstance().newTown("town");

        NewClaimedChunkStorage newClaimedChunkStorage = NewClaimedChunkStorage.getInstance();

        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(-1,-1), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(-1,0), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(-1,1), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(0,-1), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(0,1), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(1,-1), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(1,0), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(1,1), townData.getID());


        ClaimedChunk2 claimedChunk2 = NewClaimedChunkStorage.getInstance().get(world.getChunkAt(0,0));

        assertTrue(ChunkUtil.isChunkEncirecledBy(claimedChunk2, ClaimedChunk2::isClaimed));
    }


    @Test
    void testGetBorderChunks(){
        TownData townData = TownDataStorage.getInstance().newTown("town");

        NewClaimedChunkStorage newClaimedChunkStorage = NewClaimedChunkStorage.getInstance();

        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(-1,-1), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(-1,0), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(-1,1), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(0,-1), townData.getID());
        ClaimedChunk2 chunkWithoutBorders = newClaimedChunkStorage.claimTownChunk(world.getChunkAt(0,0), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(0,1), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(1,-1), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(1,0), townData.getID());
        newClaimedChunkStorage.claimTownChunk(world.getChunkAt(1,1), townData.getID());

        Collection<ClaimedChunk2> borderChunks = ChunkUtil.getBorderChunks(townData);

        assertEquals(8, borderChunks.size());
        assertFalse(borderChunks.contains(chunkWithoutBorders));
    }

    @Test
    void test_chunkContainsBuildings_noBuilding(){
        TownData townData = TownDataStorage.getInstance().newTown("town");
        NewClaimedChunkStorage newClaimedChunkStorage = NewClaimedChunkStorage.getInstance();

        TownClaimedChunk chunkFreeFromBuildings = newClaimedChunkStorage.claimTownChunk(world.getChunkAt(0,0), townData.getID());

        assertFalse(ChunkUtil.chunkContainsBuildings(chunkFreeFromBuildings, townData));
    }

    @Test
    void test_chunkContainsBuildings_withBuilding(){
        TownData townData = TownDataStorage.getInstance().newTown("town");
        NewClaimedChunkStorage newClaimedChunkStorage = NewClaimedChunkStorage.getInstance();

        TownClaimedChunk chunkWithBuilding = newClaimedChunkStorage.claimTownChunk(world.getChunkAt(0,0), townData.getID());
        townData.registerFort(new Vector3D(0,0,0,world.getUID().toString()));

        assertTrue(ChunkUtil.chunkContainsBuildings(chunkWithBuilding, townData));
    }
}