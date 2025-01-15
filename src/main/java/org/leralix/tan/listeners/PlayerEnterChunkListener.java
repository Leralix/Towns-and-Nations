package org.leralix.tan.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.WildernessChunk;
import org.leralix.tan.storage.PlayerAutoClaimStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.enums.ChunkType;
import org.leralix.tan.lang.Lang;

public class PlayerEnterChunkListener implements Listener {

    @EventHandler
    public void playerMoveEvent(final @NotNull PlayerMoveEvent e){

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


        ClaimedChunk2 currentClaimedChunk = NewClaimedChunkStorage.get(currentChunk);
        ClaimedChunk2 nextClaimedChunk = NewClaimedChunkStorage.get(nextChunk);

        //Both chunks have the same owner, no need to change
        if(equalsWithNulls(currentClaimedChunk,nextClaimedChunk)){
            return;
        }

        //Three case: Into wilderness, into town, into region
        if(nextClaimedChunk instanceof WildernessChunk){
            //If auto claim is on, claim the chunk
            if(PlayerAutoClaimStorage.containsPlayer(e.getPlayer())){
                autoClaimChunk(e, nextChunk, player);
            }
            //Else send message player enter wilderness
            else
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Lang.WILDERNESS.get()));

        }
        else {
            nextClaimedChunk.playerEnterClaimedArea(player);
        }

    }

    private void autoClaimChunk(final @NotNull PlayerMoveEvent e, final @NotNull Chunk nextChunk, final @NotNull Player player) {
        ChunkType chunkType = PlayerAutoClaimStorage.getChunkType(e.getPlayer());
        PlayerData playerStat = PlayerDataStorage.get(player.getUniqueId().toString());

        if(chunkType == ChunkType.TOWN) {
            if (!playerStat.haveTown()) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_TOWN.get());
                return;
            }
            playerStat.getTown().claimChunk(player, nextChunk);
        }
        if(chunkType == ChunkType.REGION) {
            if(!playerStat.haveRegion()){
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_REGION.get());
                return;
            }
            playerStat.getRegion().claimChunk(player, nextChunk);
        }
    }

    public static boolean equalsWithNulls(final ClaimedChunk2 a,final ClaimedChunk2 b) {
        if (a==b) return true;
        if ((a==null)||(b==null)) return false;
        return a.getOwnerID().equals(b.getOwnerID());
    }


}
