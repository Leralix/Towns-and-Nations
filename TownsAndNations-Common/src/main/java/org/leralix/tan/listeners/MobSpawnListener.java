package org.leralix.tan.listeners;

import org.bukkit.Chunk;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.leralix.tan.dataclass.chunk.ClaimedChunk;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;

public class MobSpawnListener implements Listener {

    @EventHandler
    public void entitySpawn(EntitySpawnEvent e){
        Chunk currentChunk = e.getEntity().getLocation().getChunk();
        if(!NewClaimedChunkStorage.getInstance().isChunkClaimed(currentChunk)){
            return;
        }
        ClaimedChunk claimedChunk = NewClaimedChunkStorage.getInstance().get(currentChunk);
        EntityType entityType = e.getEntity().getType();

        if(!claimedChunk.canEntitySpawn(entityType)){
            e.setCancelled(true);
        }
    }
}
