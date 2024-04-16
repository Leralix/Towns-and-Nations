package org.tan.TownsAndNations.listeners;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.newChunkData.ClaimedChunk2;
import org.tan.TownsAndNations.DataClass.newChunkData.TownClaimedChunk;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.PlayerSelectPropertyPositionStorage;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.StringUtil;

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
        if (!NewClaimedChunkStorage.isChunkClaimed(blocChunk)){
            player.sendMessage(ChatUtils.getTANString() + Lang.POSITION_NEED_TO_BE_IN_CLAIMED_CHUNK.get());
            return;

        }

        PlayerData playerData = PlayerDataStorage.get(player);
        if (!playerData.haveTown()){
            PlayerSelectPropertyPositionStorage.removePlayer(player.getUniqueId().toString());
            return;
        }

        ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.get(blocChunk);
        if (!claimedChunk.getOwnerID().equals(playerData.getTownId())){
            player.sendMessage(ChatUtils.getTANString() + Lang.POSITION_NEED_TO_BE_IN_PLAYER_TOWN.get());
            return;
        }

        PlayerSelectPropertyPositionStorage.addPoint(player, block);

    }
}
