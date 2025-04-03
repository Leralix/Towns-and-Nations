package org.leralix.tan.commands.admin;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.tan.dataclass.Level;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TownClaimedChunk;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.factory.Factory;
import org.leralix.tan.storage.SudoPlayerStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UnclaimChunkTest {

    public static CommandSender sender;
    public static Chunk chunkPosition;
    public static Chunk otherChunkPosition;
    public static World world;
    public static TownData townData;

    @BeforeAll
    static void initialise(){
        Factory.initializeConfigs();

        townData = TownDataStorage.getInstance().newTown("Cool town");


        world = Mockito.mock(World.class);
        when(world.getUID()).thenReturn(UUID.randomUUID());

        chunkPosition = Mockito.mock(Chunk.class);
        when(chunkPosition.getWorld()).thenReturn(world);
        when(chunkPosition.getX()).thenReturn(0);
        when(chunkPosition.getZ()).thenReturn(0);

        otherChunkPosition = Mockito.mock(Chunk.class);
        when(otherChunkPosition.getWorld()).thenReturn(world);
        when(otherChunkPosition.getX()).thenReturn(-1);
        when(otherChunkPosition.getZ()).thenReturn(-1);

        when(world.getChunkAt(any(Location.class))).thenReturn(chunkPosition);

        Factory.initializeConfigs();

        assertFalse(NewClaimedChunkStorage.getInstance().isChunkClaimed(chunkPosition));
        NewClaimedChunkStorage.getInstance().claimTownChunk(chunkPosition, townData.getID());
        assertTrue(NewClaimedChunkStorage.getInstance().isChunkClaimed(chunkPosition));
    }

    @Test
    void standardUse() {
        Player player = Factory.getRandomPlayer();
        when(player.getLocation()).thenReturn(new Location(world, 0, 0, 0));

        UnclaimAdminCommand unclaim = new UnclaimAdminCommand();
        unclaim.perform(player, new String[]{"unclaim"});

        assertFalse(NewClaimedChunkStorage.getInstance().isChunkClaimed(chunkPosition));
    }

    @Test
    void playerOnAnotherChunk() {
        NewClaimedChunkStorage.getInstance().claimTownChunk(chunkPosition, townData.getID());

        Player player = Factory.getRandomPlayer();
        when(player.getLocation()).thenReturn(new Location(world, 0, 0, 0));
        when(world.getChunkAt(any(Location.class))).thenReturn(otherChunkPosition);

        UnclaimAdminCommand unclaim = new UnclaimAdminCommand();
        unclaim.perform(player, new String[]{"unclaim"});

        assertTrue(NewClaimedChunkStorage.getInstance().isChunkClaimed(chunkPosition));
    }
}
