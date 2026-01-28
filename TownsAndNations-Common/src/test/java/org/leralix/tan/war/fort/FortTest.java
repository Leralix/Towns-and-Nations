package org.leralix.tan.war.fort;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.building.fort.Fort;
import org.leralix.tan.data.building.fort.FortData;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FortTest extends BasicTest {

    @Test
    void captureFortSwitchOwnershipOfChunks(){

        Player player = server.addPlayer();
        World world = server.addSimpleWorld("world");


        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        TownData defenderTown = TownDataStorage.getInstance().newTown("defender", tanPlayer);
        TownData attackerTown = TownDataStorage.getInstance().newTown("attacker");

        Fort fort = new FortData("0", new Vector3D(world.getBlockAt(100 * 16, 0, 0).getLocation()), "fort", defenderTown);

        var chunk1 = NewClaimedChunkStorage.getInstance().claimTownChunk(world.getChunkAt(100, 0), defenderTown.getID());
        var chunk2 = NewClaimedChunkStorage.getInstance().claimTownChunk(world.getChunkAt(101, 0), defenderTown.getID());

        assertFalse(chunk1.isOccupied());
        assertFalse(chunk2.isOccupied());

        fort.setOccupier(attackerTown);

        assertTrue(chunk1.isOccupied());
        assertTrue(chunk2.isOccupied());

    }

}