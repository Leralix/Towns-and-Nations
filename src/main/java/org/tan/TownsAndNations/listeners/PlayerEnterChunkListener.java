package org.tan.TownsAndNations.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.NotNull;
import org.tan.TownsAndNations.DataClass.newChunkData.ClaimedChunk2;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.ChunkType;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.storage.PlayerAutoClaimStorage;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.ChunkUtil;

public class PlayerEnterChunkListener implements Listener {

    @EventHandler
    public void PlayerMoveEvent(final @NotNull PlayerMoveEvent e){

        Chunk currentChunk = e.getFrom().getChunk();
        if(e.getTo() == null){
            return;
        }
        Chunk nextChunk = e.getTo().getChunk();

        if(currentChunk.equals(nextChunk)){
            return;
        }

        Player player = e.getPlayer();

        //If both chunks are not claimed, no need to display anything
        if(!NewClaimedChunkStorage.isChunkClaimed(currentChunk) &&
                !NewClaimedChunkStorage.isChunkClaimed(nextChunk)){

            if(PlayerAutoClaimStorage.containsPlayer(e.getPlayer())){
                autoClaimChunk(e, nextChunk, player);
            }
            return;
        }


        ClaimedChunk2 CurrentClaimedChunk = NewClaimedChunkStorage.get(currentChunk);
        ClaimedChunk2 NextClaimedChunk = NewClaimedChunkStorage.get(nextChunk);

        //Both chunks have the same owner, no need to change
        if(equalsWithNulls(CurrentClaimedChunk,NextClaimedChunk)){
            return;
        }

        //Three case: Into wilderness, into town, into region
        if(NextClaimedChunk == null){
            //If auto claim is on, claim the chunk
            if(PlayerAutoClaimStorage.containsPlayer(e.getPlayer())){
                autoClaimChunk(e, nextChunk, player);
            }
            //Else send message player enter wilderness
            else
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Lang.CHUNK_ENTER_WILDERNESS.get()));

        }
        else {
            NextClaimedChunk.playerEnterClaimedArea(player);
        }

    }

    private void autoClaimChunk(final @NotNull PlayerMoveEvent e, final @NotNull Chunk nextChunk, final @NotNull Player player) {
        ChunkType chunkType = PlayerAutoClaimStorage.getChunkType(e.getPlayer());

        switch (chunkType){
            case TOWN:
                ChunkUtil.claimChunkForTown(player, nextChunk);
                break;
            case REGION:
                ChunkUtil.claimChunkForRegion(player, nextChunk);
                break;
        }
    }

    public static boolean equalsWithNulls(final ClaimedChunk2 a,final ClaimedChunk2 b) {
        if (a==b) return true;
        if ((a==null)||(b==null)) return false;
        return a.getOwnerID().equals(b.getOwnerID());
    }


}
