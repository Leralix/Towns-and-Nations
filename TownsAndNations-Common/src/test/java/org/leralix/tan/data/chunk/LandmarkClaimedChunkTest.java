package org.leralix.tan.data.chunk;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.building.landmark.Landmark;
import org.leralix.tan.data.territory.Town;

import static org.junit.jupiter.api.Assertions.*;

class LandmarkClaimedChunkTest extends BasicTest {

    private World world;


    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        world = server.addSimpleWorld("world");
    }

    @Override
    @AfterEach
    protected void tearDown() {
        super.tearDown();
    }

    @Test
    void CreateLandmarkClaimTheChunkTest() {

        Chunk chunk = world.getChunkAt(10, 5);

        TownsAndNations.getPlugin().getLandmarkStorage().addLandmark(new Location(
                world,
                (double) chunk.getX() * 16,
                64,
                (double) chunk.getZ() * 16)
        );

        IClaimedChunk claimedChunk = claimStorage.get(chunk);
        assertInstanceOf(LandmarkClaimedChunk.class, claimedChunk);
    }

    @Test
    void UnclaimBecauseNotEncircledTest() {

        //Initialisation
        Chunk chunk = world.getChunkAt(20, 20);
        Landmark landmark = TownsAndNations.getPlugin().getLandmarkStorage().addLandmark(new Location(world,
                (double) chunk.getX() * 16,
                64,
                (double) chunk.getZ() * 16)
        );

        Town townData = townStorage.newTown("town");
        landmark.setOwner(townData);

        assertTrue(landmark.isOwned());
        assertTrue(landmark.isOwnedBy(townData));

        IClaimedChunk claimedChunk = claimStorage.get(chunk);

        // Simulate an update that would check for encirclement. Since no claimed chunk are adjacent, it should unclaim itself.
        claimedChunk.notifyUpdate();

        assertFalse(landmark.isOwned());
        assertFalse(landmark.isOwnedBy(townData));
    }

    @Test
    void NotUnclaimBecauseEncircledTest() {

        Chunk chunk = world.getChunkAt(-5, 5);

        Landmark landmark = TownsAndNations.getPlugin().getLandmarkStorage().addLandmark(new Location(world,
                (double) chunk.getX() * 16,
                64,
                (double) chunk.getZ() * 16)
        );

        Town townData = townStorage.newTown("town");


        for(IClaimedChunk adjacent : claimStorage.getEightAjacentChunks(claimStorage.get(chunk))){
            claimStorage.claimTownChunk(adjacent.getChunk(), townData.getID());
        }

        landmark.setOwner(townData);

        assertTrue(landmark.isOwned());
        assertTrue(landmark.isOwnedBy(townData));

        IClaimedChunk claimedChunk = claimStorage.get(chunk);

        // Simulate an update that would check for encirclement. Since all adjacent chunks are claimed, it should remain claimed.
        claimedChunk.notifyUpdate();

        assertTrue(landmark.isOwned());
        assertTrue(landmark.isOwnedBy(townData));
    }
}