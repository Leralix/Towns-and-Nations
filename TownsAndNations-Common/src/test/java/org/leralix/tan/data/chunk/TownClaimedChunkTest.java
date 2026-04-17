package org.leralix.tan.data.chunk;

import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.position.Vector2D;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.building.fort.Fort;
import org.leralix.tan.data.territory.Town;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TownClaimedChunkTest extends BasicTest {


    private World world;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        world = server.addSimpleWorld("world");
    }

    @Test
    void notifyUpdate() {

        Town townData = townStorage.newTown("Town");

        claimStorage.claimTownChunk(world.getChunkAt(1, 0), townData.getID());
        var middleChunk = claimStorage.claimTownChunk(world.getChunkAt(2, 0), townData.getID());
        claimStorage.claimTownChunk(world.getChunkAt(3, 0), townData.getID());

        claimStorage.unclaimChunkAndUpdate(middleChunk);

        assertEquals(0, claimStorage.getAllChunks().size());
    }

    @Test
    void notifyUpdateWithCapital() {

        Town townData = townStorage.newTown("Town");

        claimStorage.claimTownChunk(world.getChunkAt(1, 0), townData.getID());
        townData.setCapitalLocation(new Vector2D(1,0, world.getUID().toString()));
        var chunk = claimStorage.claimTownChunk(world.getChunkAt(2, 0), townData.getID());
        claimStorage.claimTownChunk(world.getChunkAt(3, 0), townData.getID());

        claimStorage.unclaimChunkAndUpdate(chunk);

        assertEquals(1, claimStorage.getAllChunks().size());
    }

    @Test
    void notifyUpdateWithOneFort() {

        Town townData = townStorage.newTown("Town");

        ChunkData claimedChunkToKeep = claimStorage.claimTownChunk(world.getChunkAt(0, 1), townData.getID());
        Fort fort = fortDataStorage.register(new Vector3D(0, 0, 0, world.getUID().toString()), townData);
        townData.addOwnedFort(fort);

        TerritoryChunkData townClaimedChunk = new TownClaimedChunk(world.getChunkAt(0, 0), townData.getID());
        townClaimedChunk.notifyUpdate();

        assertEquals(1, claimStorage.getAllChunks().size());
        assertTrue(claimStorage.getAllChunks().contains(claimedChunkToKeep));
    }
}