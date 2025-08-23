package org.leralix.tan.utils.territory;

import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;

class ChunkUtilTest extends BasicTest {

    private World world;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        world = server.addSimpleWorld("world");
    }

    @Test
    void testIsChunkEncirecledByValid() {

        ClaimedChunk2 claimedChunk2 = NewClaimedChunkStorage.getInstance().get(world.getChunkAt(0,0));

        ChunkUtil.isChunkEncirecledBy(claimedChunk2, claimedChunk -> !claimedChunk.isClaimed());
    }

    @Test
    void testIsChunkEncirecledByInvalid() {
        ClaimedChunk2 claimedChunk2 = NewClaimedChunkStorage.getInstance().get(world.getChunkAt(0,0));

        ChunkUtil.isChunkEncirecledBy(claimedChunk2, claimedChunk -> !claimedChunk.isClaimed());
    }

}