package org.leralix.tan.war.cosmetic;

import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.territory.ChunkUtil;
import org.leralix.tan.war.PlannedAttack;
import org.leralix.tan.war.War;
import org.leralix.tan.war.legacy.CreateAttackData;
import org.leralix.tan.war.legacy.CurrentAttack;
import org.leralix.tan.war.legacy.WarRole;
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

        var townAttacker = TownDataStorage.getInstance().newTown("AttackerTown");
        var townDefender = TownDataStorage.getInstance().newTown("DefenderTown", tanDefender);

        War war = new War("TestWar", townAttacker, townDefender);
        CreateAttackData createAttackData = new CreateAttackData(war, WarRole.MAIN_ATTACKER);
        PlannedAttack plannedAttack = new PlannedAttack("TetsWar", createAttackData);
        CurrentAttack currentAttack = new CurrentAttack(plannedAttack, 0, 0);

        townDefender.claimChunk(defender, world.getChunkAt(0, 0));
        townDefender.claimChunk(defender, world.getChunkAt(0, 1));


        var list = ChunkUtil.getChunksInRadius(world.getChunkAt(0, 0), 1);

        List<ChunkLine> chunkLines = ShowBoundaries.sortChunkLines(list, List.of(currentAttack));

        // Two chunks -> 3 faces each
        assertEquals(6, chunkLines.size());
    }


}