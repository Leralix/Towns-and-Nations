package org.leralix.tan.dataclass.chunk;

import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.position.Vector2D;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.BasicTest;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TownClaimedChunkTest extends BasicTest {


    private NewClaimedChunkStorage claimedChunkStorage;
    private World world;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        claimedChunkStorage = NewClaimedChunkStorage.getInstance();
        world = server.addSimpleWorld("world");
        TownsAndNations.getPlugin().resetSingletonForTests();
    }

    @Test
    void notifyUpdate() {

        TownData townData = TownDataStorage.getInstance().newTown("Town");

        claimedChunkStorage.claimTownChunk(world.getChunkAt(1, 0), townData.getID());
        claimedChunkStorage.claimTownChunk(world.getChunkAt(0, 1), townData.getID());
        TerritoryChunk townClaimedChunk = new TownClaimedChunk(world.getChunkAt(0, 0), townData.getID());

        townClaimedChunk.notifyUpdate();

        assertEquals(0, claimedChunkStorage.getClaimedChunksMap().size());
    }

    @Test
    void notifyUpdateWithCapital() {

        TownData townData = TownDataStorage.getInstance().newTown("Town");

        claimedChunkStorage.claimTownChunk(world.getChunkAt(1, 0), townData.getID());
        townData.setCapitalLocation(new Vector2D(1,0, world.getUID().toString()));
        claimedChunkStorage.claimTownChunk(world.getChunkAt(0, 1), townData.getID());

        TerritoryChunk townClaimedChunk = new TownClaimedChunk(world.getChunkAt(0, 0), townData.getID());
        townClaimedChunk.notifyUpdate();

        assertEquals(1, claimedChunkStorage.getClaimedChunksMap().size());
    }

    @Test
    void notifyUpdateWithOneFort() {

        TownData townData = TownDataStorage.getInstance().newTown("Town");

        claimedChunkStorage.claimTownChunk(world.getChunkAt(1, 0), townData.getID());
        ClaimedChunk claimedChunkToKeep = claimedChunkStorage.claimTownChunk(world.getChunkAt(0, 1), townData.getID());
        townData.registerFort(new Vector3D(10, 0, 20, world.getUID().toString()));

        TerritoryChunk townClaimedChunk = new TownClaimedChunk(world.getChunkAt(0, 0), townData.getID());
        townClaimedChunk.notifyUpdate();

        assertEquals(1, claimedChunkStorage.getClaimedChunksMap().size());
        assertTrue(claimedChunkStorage.getClaimedChunksMap().containsValue(claimedChunkToKeep));
    }
}