package org.leralix.tan.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.WildernessChunk;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.enums.ChunkType;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.PlayerAutoClaimStorage;
import org.leralix.tan.utils.ChunkUtil;

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
        if(NextClaimedChunk instanceof WildernessChunk){
            //If auto claim is on, claim the chunk
            if(PlayerAutoClaimStorage.containsPlayer(e.getPlayer())){
                autoClaimChunk(e, nextChunk, player);
            }
            //Else send message player enter wilderness
            else
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Lang.WILDERNESS.get()));

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
