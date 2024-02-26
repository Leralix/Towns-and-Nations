package org.tan.TownsAndNations.listeners;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.tan.TownsAndNations.DataClass.newChunkData.ClaimedChunk2;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.utils.ChatUtils;

public class PlayerEnterChunkListener implements Listener {

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent e){


        Chunk currentChunk = e.getFrom().getChunk();
        if(e.getTo() == null){
            return;
        }
        Chunk nextChunk = e.getTo().getChunk();

        if(currentChunk.equals(nextChunk)){
            return;
        }

        if(!NewClaimedChunkStorage.isChunkClaimed(currentChunk) &&
                !NewClaimedChunkStorage.isChunkClaimed(nextChunk)){
            return;
        }


        ClaimedChunk2 CurrentClaimedChunk = NewClaimedChunkStorage.get(currentChunk);
        ClaimedChunk2 NextClaimedChunk = NewClaimedChunkStorage.get(nextChunk);

        //Both chunks have the same owner, no need to change
        if(equalsWithNulls(CurrentClaimedChunk,NextClaimedChunk)){
            return;
        }

        Player player = e.getPlayer();


        //Three case: Into wilderness, into town, into region
        if(NextClaimedChunk == null){
            player.sendMessage(ChatUtils.getTANString() + Lang.CHUNK_ENTER_WILDERNESS.get());
            return;
        }
        else {
            NextClaimedChunk.playerEnterClaimedArea(player);
            return;
        }



    }

    public static final boolean equalsWithNulls(Object a, Object b) {
        if (a==b) return true;
        if ((a==null)||(b==null)) return false;
        return a.equals(b);
    }


}
