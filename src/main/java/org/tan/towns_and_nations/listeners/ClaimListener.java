package org.tan.towns_and_nations.listeners;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class ClaimListener implements Listener {


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Chunk chunk = event.getBlock().getChunk();
        Player player = event.getPlayer();

        if (chunkClaimer.isChunkClaimed(chunk)) {
            if (!chunkClaimer.isOwner(chunk, player)) {
                player.sendMessage("Ce chunk a été claim, vous ne pouvez pas casser de blocs ici.");
                event.setCancelled(true);
            }
        }
    }
}
