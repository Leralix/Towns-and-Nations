package org.leralix.tan.data.chunk;

import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.position.Vector2D;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.BasicTest;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.building.fort.Fort;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.storage.stored.FortStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;

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

        TownData townData = townDataStorage.newTown("Town");

        claimedChunkStorage.claimTownChunk(world.getChunkAt(1, 0), townData.getID());
        var middleChunk = claimedChunkStorage.claimTownChunk(world.getChunkAt(2, 0), townData.getID());
        claimedChunkStorage.claimTownChunk(world.getChunkAt(3, 0), townData.getID());

        claimedChunkStorage.unclaimChunkAndUpdate(middleChunk);

        assertEquals(0, claimedChunkStorage.getClaimedChunksMap().size());
    }

    @Test
    void notifyUpdateWithCapital() {

        TownData townData = townDataStorage.newTown("Town");

        claimedChunkStorage.claimTownChunk(world.getChunkAt(1, 0), townData.getID());
        townData.setCapitalLocation(new Vector2D(1,0, world.getUID().toString()));
        var chunk = claimedChunkStorage.claimTownChunk(world.getChunkAt(2, 0), townData.getID());
        claimedChunkStorage.claimTownChunk(world.getChunkAt(3, 0), townData.getID());

        NewClaimedChunkStorage.getInstance().unclaimChunkAndUpdate(chunk);

        assertEquals(1, claimedChunkStorage.getClaimedChunksMap().size());
    }

    @Test
    void notifyUpdateWithOneFort() {

        TownData townData = townDataStorage.newTown("Town");

        ClaimedChunk claimedChunkToKeep = claimedChunkStorage.claimTownChunk(world.getChunkAt(0, 1), townData.getID());
        Fort fort = FortStorage.getInstance().register(new Vector3D(0, 0, 0, world.getUID().toString()), townData);
        townData.addOwnedFort(fort);

        TerritoryChunk townClaimedChunk = new TownClaimedChunk(world.getChunkAt(0, 0), townData.getID());
        townClaimedChunk.notifyUpdate();

        assertEquals(1, claimedChunkStorage.getClaimedChunksMap().size());
        assertTrue(claimedChunkStorage.getClaimedChunksMap().containsValue(claimedChunkToKeep));
    }
}