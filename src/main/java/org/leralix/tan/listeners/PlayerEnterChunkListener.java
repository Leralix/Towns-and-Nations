package org.leralix.tan.listeners;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.WildernessChunk;
import org.leralix.tan.enums.ChunkType;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.PlayerAutoClaimStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

public class PlayerEnterChunkListener implements Listener {

    private final boolean displayTerritoryNamewithColor;

    public PlayerEnterChunkListener(){
        displayTerritoryNamewithColor = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("displayTerritoryNameWithOwnColor");
    }

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
        if(!NewClaimedChunkStorage.getInstance().isChunkClaimed(currentChunk) &&
                !NewClaimedChunkStorage.getInstance().isChunkClaimed(nextChunk)){

            if(PlayerAutoClaimStorage.containsPlayer(e.getPlayer())){
                autoClaimChunk(e, nextChunk, player);
            }
            return;
        }


        ClaimedChunk2 currentClaimedChunk = NewClaimedChunkStorage.getInstance().get(currentChunk);
        ClaimedChunk2 nextClaimedChunk = NewClaimedChunkStorage.getInstance().get(nextChunk);

        //Both chunks have the same owner, no need to change
        if(sameOwner(currentClaimedChunk,nextClaimedChunk)){
            return;
        }

        nextClaimedChunk.playerEnterClaimedArea(player, displayTerritoryNamewithColor);


        if(nextClaimedChunk instanceof WildernessChunk &&
                PlayerAutoClaimStorage.containsPlayer(e.getPlayer())){
            autoClaimChunk(e, nextChunk, player);
        }
    }

    private void autoClaimChunk(final @NotNull PlayerMoveEvent e, final @NotNull Chunk nextChunk, final @NotNull Player player) {
        ChunkType chunkType = PlayerAutoClaimStorage.getChunkType(e.getPlayer());
        ITanPlayer playerStat = PlayerDataStorage.getInstance().get(player.getUniqueId().toString());

        if(chunkType == ChunkType.TOWN) {
            if (!playerStat.hasTown()) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_TOWN.get());
                return;
            }
            playerStat.getTown().claimChunk(player, nextChunk);
        }
        if(chunkType == ChunkType.REGION) {
            if(!playerStat.hasRegion()){
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_REGION.get());
                return;
            }
            playerStat.getRegion().claimChunk(player, nextChunk);
        }
    }

    public static boolean sameOwner(final ClaimedChunk2 a, final ClaimedChunk2 b) {
        if (a==b) return true;
        return a.getOwnerID().equals(b.getOwnerID());
    }


}
