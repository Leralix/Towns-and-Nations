package org.tan.TownsAndNations.listeners;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;

public class MobSpawnListener implements Listener {


    @EventHandler
    public void PlayerMoveEvent(EntitySpawnEvent e){
        Chunk currentChunk = e.getEntity().getLocation().getChunk();
        if(!NewClaimedChunkStorage.isChunkClaimed(currentChunk)){
            return;
        }
        TownData town = NewClaimedChunkStorage.getChunkOwnerTown(currentChunk);
        if(town == null)
            return;
        String entityType = e.getEntity().getType().toString();
        if(!town.getChunkSettings().getSpawnControl(entityType).isActivated())
            e.setCancelled(true);
    }
}
