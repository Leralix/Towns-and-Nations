package org.leralix.tan.listeners.interact.events;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.chunk.IClaimedChunk;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.ClaimChunkValidationResult;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.CreateTown;
import org.leralix.tan.listeners.interact.ListenerState;
import org.leralix.tan.listeners.interact.RightClickListenerEvent;
import org.leralix.tan.storage.ClaimBlacklistStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.territory.ChunkUtil;
import org.leralix.tan.utils.text.TanChatUtils;

/**
 * In case
 */
public class ChooseCapital extends RightClickListenerEvent {

    private final ITanPlayer playerData;
    private final int cost;

    public ChooseCapital(ITanPlayer playerData, int cost){
        this.playerData = playerData;
        this.cost = cost;
    }

    @Override
    public ListenerState execute(PlayerInteractEvent event) {

        Block block = event.getClickedBlock();
        if(block == null){
            return ListenerState.FAILURE;
        }
        Chunk selectedChunk = block.getChunk();

        ClaimChunkValidationResult result = test(selectedChunk);

        if(result.isSuccess()){
            TanChatUtils.message(event.getPlayer(), Lang.PLAYER_WRITE_TOWN_NAME_IN_CHAT.get(playerData));
            PlayerChatListenerStorage.register(event.getPlayer(), playerData.getLang(), new CreateTown(cost, selectedChunk));
            return ListenerState.SUCCESS;
        }
        else {
            TanChatUtils.message(playerData.getPlayer(), result.getErrorMessage());
            return ListenerState.FAILURE;
        }

    }

    public ClaimChunkValidationResult test(Chunk chunk){
        if (ClaimBlacklistStorage.cannotBeClaimed(chunk)) {
            return ClaimChunkValidationResult.failure(Lang.CHUNK_IS_BLACKLISTED.get());
        }
        IClaimedChunk territoryChunk = TownsAndNations.getPlugin().getClaimStorage().get(chunk);

        int bufferRadius = Constants.territoryClaimBufferZone();
        // If the chunk is in the buffer zone of another territory, it cannot be claimed.
        if (ChunkUtil.isInBufferZone(territoryChunk, null, bufferRadius)) {
            return ClaimChunkValidationResult.failure(Lang.CHUNK_IN_BUFFER_ZONE.get(Integer.toString(bufferRadius)));
        }
        return ClaimChunkValidationResult.success();
    }
}
