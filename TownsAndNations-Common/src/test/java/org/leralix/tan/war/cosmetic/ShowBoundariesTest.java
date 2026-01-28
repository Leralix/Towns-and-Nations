package org.leralix.tan.war.cosmetic;

import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.territory.ChunkUtil;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShowBoundariesTest extends BasicTest {

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
    }


    @Test
    void test_getChunksInRadius_no_chunks() {

        PlayerMock defender = server.addPlayer();
        ITanPlayer tanDefender = PlayerDataStorage.getInstance().get(defender);
        World world = server.addSimpleWorld("world");

        var townDefender = TownDataStorage.getInstance().newTown("DefenderTown", tanDefender);

        townDefender.addToBalance(townDefender.getClaimCost() * 10);

        townDefender.claimChunk(defender, world.getChunkAt(0, 0));
        townDefender.claimChunk(defender, world.getChunkAt(0, 1));


        var list = ChunkUtil.getChunksInRadius(world.getChunkAt(0, 0), 1);

        List<ChunkLine> chunkLines = ShowBoundaries.sortChunkLines(list, tanDefender);

        // Two chunks -> 3 faces each
        assertEquals(2, townDefender.getNumberOfClaimedChunk());
        assertEquals(6, chunkLines.size());
    }


}