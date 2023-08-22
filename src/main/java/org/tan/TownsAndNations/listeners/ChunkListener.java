package org.tan.TownsAndNations.listeners;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.TownChunkPermission;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.storage.ClaimedChunkStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class ChunkListener implements Listener {

    @EventHandler
    public void OnBlockBreak(BlockBreakEvent event){

        Chunk chunk = event.getBlock().getLocation().getChunk();

        if(!ClaimedChunkStorage.isChunkClaimed(chunk))
            return;
        Player player = event.getPlayer();

        TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwner(chunk));
        TownData playerTown = TownDataStorage.get(player);

        //Same town
        if(ClaimedChunkStorage.getChunkOwner(chunk).equals(playerTown.getID()))
            return;
        //Same alliance
        if(chunkTown.getChunkSettings().getBreakAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getID()))
            return;
        //permission is on foreign
        if(chunkTown.getChunkSettings().getBreakAuth() == TownChunkPermission.FOREIGN)
            return;

        playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(chunk));
        event.setCancelled(true);
    }

    @EventHandler
    public void OnContainersOpen(PlayerInteractEvent event){

        Block block = event.getClickedBlock();
        if (block != null){
            String blockName = block.getType().name();

            Chunk chunk = block.getLocation().getChunk();
            Player player = event.getPlayer();

            if(!ClaimedChunkStorage.isChunkClaimed(chunk))
                return;

            if(blockName.equals("CHEST")){


                TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwner(chunk));
                TownData playerTown = TownDataStorage.get(player);

                //Same town
                if(ClaimedChunkStorage.getChunkOwner(chunk).equals(playerTown.getID()))
                    return;
                //Same alliance
                if(chunkTown.getChunkSettings().getChestAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getID()))
                    return;
                //permission is on foreign
                if(chunkTown.getChunkSettings().getChestAuth() == TownChunkPermission.FOREIGN)
                    return;

                playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(chunk));
                event.setCancelled(true);

            }
            else if(blockName.contains("DOOR")){

                TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwner(chunk));
                TownData playerTown = TownDataStorage.get(player);

                //Same town
                if(ClaimedChunkStorage.getChunkOwner(chunk).equals(playerTown.getID()))
                    return;
                //Same alliance
                if(chunkTown.getChunkSettings().getDoorAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getID()))
                    return;
                //permission is on foreign
                if(chunkTown.getChunkSettings().getDoorAuth() == TownChunkPermission.FOREIGN)
                    return;

                playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(chunk));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void OnBlocPlaced(BlockPlaceEvent event){

        Block block = event.getBlock();
        Chunk chunk = block.getLocation().getChunk();
        if(!ClaimedChunkStorage.isChunkClaimed(chunk)){
            return;
        }

        Player player = event.getPlayer();

        TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwner(chunk));
        TownData playerTown = TownDataStorage.get(player);

        //Same town
        if(ClaimedChunkStorage.getChunkOwner(chunk).equals(playerTown.getID()))
            return;
        //Same alliance
        if(chunkTown.getChunkSettings().getPlaceAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getID()))
            return;
        //Permission is on foreign
        if(chunkTown.getChunkSettings().getPlaceAuth() == TownChunkPermission.FOREIGN)
            return;

        playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(chunk));
        event.setCancelled(true);
    }

    private void playerCantPerformAction(Player player, String ChunkOwner){
        player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());
        player.sendMessage(getTANString() + Lang.CHUNK_BELONGS_TO.getTranslation(ChunkOwner));
    }

}
