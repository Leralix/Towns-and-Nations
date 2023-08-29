package org.tan.TownsAndNations.listeners;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.storage.ClaimedChunkStorage;

public class PlayerEnterChunkListener implements Listener {

    @EventHandler
    public void PlayerMoveEvent(Player player, Location from, Location to){

        Chunk currentChunk = from.getChunk();
        Chunk nextChunk = to.getChunk();

        if(currentChunk.equals(nextChunk)){
            return;
        }

        TownData townFrom = ClaimedChunkStorage.getChunkOwnerTown(currentChunk);
        TownData townTo = ClaimedChunkStorage.getChunkOwnerTown(nextChunk);

        if(equalsWithNulls(townFrom,townTo)){
            return;
        }

        if(townFrom == null){
            player.sendMessage("Wilderness");
        }
        else{
            player.sendMessage("You enter: " + townTo.getName());
        }


    }

    public static final boolean equalsWithNulls(Object a, Object b) {
        if (a==b) return true;
        if ((a==null)||(b==null)) return false;
        return a.equals(b);
    }


}
