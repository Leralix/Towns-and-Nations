package org.leralix.tan.dataclass.chunk;

import static org.junit.jupiter.api.Assertions.*;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

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

    LandmarkStorage.getInstance()
        .addLandmark(
            new Location(world, (double) chunk.getX() * 16, 64, (double) chunk.getZ() * 16));

    ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.getInstance().get(chunk);
    assertInstanceOf(LandmarkClaimedChunk.class, claimedChunk);
  }

  @Test
  void UnclaimBecauseNotEncircledTest() {

    // Initialisation
    Chunk chunk = world.getChunkAt(20, 20);
    Landmark landmark =
        LandmarkStorage.getInstance()
            .addLandmark(
                new Location(world, (double) chunk.getX() * 16, 64, (double) chunk.getZ() * 16));

    TownData townData = TownDataStorage.getInstance().newTown("town").join();
    landmark.setOwner(townData);

    assertTrue(landmark.isOwned());
    assertTrue(landmark.isOwnedBy(townData));

    ClaimedChunk2 claimedChunk2 = NewClaimedChunkStorage.getInstance().get(chunk);

    // Simulate an update that would check for encirclement. Since no claimed chunk are adjacent, it
    // should unclaim itself.
    claimedChunk2.notifyUpdate();

    assertFalse(landmark.isOwned());
    assertFalse(landmark.isOwnedBy(townData));
  }

  @Test
  void NotUnclaimBecauseEncircledTest() {

    // Initialisation
    NewClaimedChunkStorage claimedChunkStorage = NewClaimedChunkStorage.getInstance();

    Chunk chunk = world.getChunkAt(-5, 5);

    Landmark landmark =
        LandmarkStorage.getInstance()
            .addLandmark(
                new Location(world, (double) chunk.getX() * 16, 64, (double) chunk.getZ() * 16));

    TownData townData = TownDataStorage.getInstance().newTown("town").join();

    for (ClaimedChunk2 adjacent :
        claimedChunkStorage.getEightAjacentChunks(claimedChunkStorage.get(chunk))) {
      claimedChunkStorage.claimTownChunk(adjacent.getChunk(), townData.getID());
    }

    landmark.setOwner(townData);

    assertTrue(landmark.isOwned());
    assertTrue(landmark.isOwnedBy(townData));

    ClaimedChunk2 claimedChunk2 = NewClaimedChunkStorage.getInstance().get(chunk);

    // Simulate an update that would check for encirclement. Since all adjacent chunks are claimed,
    // it should remain claimed.
    claimedChunk2.notifyUpdate();

    assertTrue(landmark.isOwned());
    assertTrue(landmark.isOwnedBy(townData));
  }
}
