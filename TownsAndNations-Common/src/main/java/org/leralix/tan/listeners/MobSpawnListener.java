package org.leralix.tan.listeners;

import org.bukkit.Chunk;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.leralix.tan.data.chunk.IClaimedChunk;
import org.leralix.tan.storage.stored.ClaimStorage;

public class MobSpawnListener implements Listener {

    private final ClaimStorage claimStorage;

    public MobSpawnListener(ClaimStorage claimStorage){
        this.claimStorage = claimStorage;
    }

    @EventHandler
    public void entitySpawn(EntitySpawnEvent e){
        Chunk currentChunk = e.getEntity().getLocation().getChunk();
        if(!claimStorage.isChunkClaimed(currentChunk)){
            return;
        }
        IClaimedChunk claimedChunk = claimStorage.get(currentChunk);
        EntityType entityType = e.getEntity().getType();

        if(!claimedChunk.canEntitySpawn(entityType)){
            e.setCancelled(true);
        }
    }
}
