package org.leralix.tan.commands.admin;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.permissions.PermissionAttachment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UnclaimChunkTest {

    private ServerMock server;

    private WorldMock world;
    private PlayerMock playerMock;
    private PermissionAttachment attachment;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();

        MockBukkit.load(SphereLib.class);
        var mockedTan = MockBukkit.load(TownsAndNations.class);

        world = server.addSimpleWorld("world");

        playerMock = server.addPlayer("TestPlayer");

        attachment = playerMock.addAttachment(mockedTan);
        attachment.setPermission("tan.admin.commands", true);
        attachment.setPermission("tan.admin.commands.unclaim", true);

    }

    @AfterEach
    public void tearDown()
    {
        MockBukkit.unmock();
    }


    @Test
    void standardUse() {

        playerMock.teleport(new Location(world, 0, 10, 0));

        TownData town = TownDataStorage.getInstance().newTown("town");

        Chunk chunkToUnclaim = world.getChunkAt(0,0);
        NewClaimedChunkStorage.getInstance().claimTownChunk(chunkToUnclaim, town.getID());

        server.dispatchCommand(playerMock, "tanadmin unclaim");

        assertFalse(NewClaimedChunkStorage.getInstance().isChunkClaimed(chunkToUnclaim));
    }

    @Test
    void playerOnAnotherChunk() {

        Chunk chunkToFailToUnclaim = world.getChunkAt(0,0);

        assertFalse(NewClaimedChunkStorage.getInstance().isChunkClaimed(chunkToFailToUnclaim));

        TownData town = TownDataStorage.getInstance().newTown("town");
        NewClaimedChunkStorage.getInstance().claimTownChunk(chunkToFailToUnclaim, town.getID());

        playerMock.teleport(new Location(world, -8, 10, -8));
        server.dispatchCommand(playerMock, "tanadmin unclaim");


        // Player is not on the claimed chunk
        assertTrue(NewClaimedChunkStorage.getInstance().isChunkClaimed(chunkToFailToUnclaim));
    }

    @Test
    void playerNoPermission() {

        Chunk chunkToFailToUnclaim = world.getChunkAt(0,0);
        assertFalse(NewClaimedChunkStorage.getInstance().isChunkClaimed(chunkToFailToUnclaim));


        playerMock.removeAttachment(attachment);
        playerMock.teleport(new Location(world, 0, 10, 0));
        TownData town = TownDataStorage.getInstance().newTown("town");
        NewClaimedChunkStorage.getInstance().claimTownChunk(chunkToFailToUnclaim, town.getID());

        server.dispatchCommand(playerMock, "tanadmin unclaim");


        // Player does not have permission
        assertTrue(NewClaimedChunkStorage.getInstance().isChunkClaimed(chunkToFailToUnclaim));
    }
}
