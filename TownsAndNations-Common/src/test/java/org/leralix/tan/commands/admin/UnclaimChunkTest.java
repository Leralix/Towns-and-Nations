package org.leralix.tan.commands.admin;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.permissions.PermissionAttachment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.territory.Town;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnclaimChunkTest extends BasicTest {

    private WorldMock world;
    private PlayerMock playerMock;
    private PermissionAttachment attachment;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        
        world = server.addSimpleWorld("world");

        playerMock = server.addPlayer("TestPlayer");

        attachment = playerMock.addAttachment(townsAndNations);
        attachment.setPermission("tan.admin.commands", true);
        attachment.setPermission("tan.admin.commands.unclaim", true);

    }

    @Test
    void standardUse() {

        playerMock.teleport(new Location(world, 0, 10, 0));

        Town town = TownsAndNations.getPlugin().getTownStorage().newTown("town");

        Chunk chunkToUnclaim = world.getChunkAt(0,0);
        claimStorage.claimTownChunk(chunkToUnclaim, town.getID());

        server.dispatchCommand(playerMock, "tanadmin unclaim");

        assertFalse(claimStorage.isChunkClaimed(chunkToUnclaim));
    }

    @Test
    void playerOnAnotherChunk() {

        Chunk chunkToFailToUnclaim = world.getChunkAt(0,0);

        assertFalse(claimStorage.isChunkClaimed(chunkToFailToUnclaim));

        Town town = TownsAndNations.getPlugin().getTownStorage().newTown("town");
        claimStorage.claimTownChunk(chunkToFailToUnclaim, town.getID());

        playerMock.teleport(new Location(world, -8, 10, -8));
        server.dispatchCommand(playerMock, "tanadmin unclaim");


        // Player is not on the claimed chunk
        assertTrue(claimStorage.isChunkClaimed(chunkToFailToUnclaim));
    }

    @Test
    void playerNoPermission() {

        Chunk chunkToFailToUnclaim = world.getChunkAt(0,0);
        assertFalse(claimStorage.isChunkClaimed(chunkToFailToUnclaim));


        playerMock.removeAttachment(attachment);
        playerMock.teleport(new Location(world, 0, 10, 0));
        Town town = TownsAndNations.getPlugin().getTownStorage().newTown("town");
        claimStorage.claimTownChunk(chunkToFailToUnclaim, town.getID());

        server.dispatchCommand(playerMock, "tanadmin unclaim");


        // Player does not have permission
        assertTrue(claimStorage.isChunkClaimed(chunkToFailToUnclaim));
    }
}
