package org.tan.TownsAndNations.listeners;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.storage.ClaimedChunkStorage;

public class PlayerEnterChunkListener implements Listener {

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent e){

        /*
        Chunk currentChunk = e.getFrom().getChunk();
        if(e.getTo() == null){
            return;
        }
        Chunk nextChunk = e.getTo().getChunk();
        Player player = e.getPlayer();

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
         */


    }

    public static final boolean equalsWithNulls(Object a, Object b) {
        if (a==b) return true;
        if ((a==null)||(b==null)) return false;
        return a.equals(b);
    }


}
