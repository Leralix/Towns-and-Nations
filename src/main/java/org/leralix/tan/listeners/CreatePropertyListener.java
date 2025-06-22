package org.leralix.tan.listeners;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.PlayerSelectPropertyPositionStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.TanChatUtils;

public class CreatePropertyListener implements Listener {


    @EventHandler
    public void OnPlayerInteractEvent(PlayerInteractEvent event) {
        if(event.getHand() != EquipmentSlot.OFF_HAND)
            return;

        Player player = event.getPlayer();
        if (!PlayerSelectPropertyPositionStorage.contains(player))
            return;

        Block block = event.getClickedBlock();
        if (block == null)
            return;

        Chunk blocChunk = block.getChunk();
        if (!NewClaimedChunkStorage.getInstance().isChunkClaimed(blocChunk)){
            player.sendMessage(TanChatUtils.getTANString() + Lang.POSITION_NEED_TO_BE_IN_CLAIMED_CHUNK.get());
            return;

        }

        ITanPlayer ITanPlayer = PlayerDataStorage.getInstance().get(player);
        if (!ITanPlayer.hasTown()){
            PlayerSelectPropertyPositionStorage.removePlayer(player);
            return;
        }

        ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.getInstance().get(blocChunk);
        if (!claimedChunk.getOwnerID().equals(ITanPlayer.getTownId())){
            player.sendMessage(TanChatUtils.getTANString() + Lang.POSITION_NEED_TO_BE_IN_PLAYER_TOWN.get());
            return;
        }

        PlayerSelectPropertyPositionStorage.addPoint(player, block, event.getBlockFace());

    }
}
