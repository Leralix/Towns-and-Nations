package org.tan.TownsAndNations.listeners;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;
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
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;
import org.tan.TownsAndNations.storage.WarTaggedPlayer;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class ChunkListener implements Listener {

    @EventHandler
    public void OnBlockBreak(BlockBreakEvent event){

        Chunk chunk = event.getBlock().getLocation().getChunk();

        if(!ClaimedChunkStorage.isChunkClaimed(chunk))
            return;
        Player player = event.getPlayer();

        TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));
        TownData playerTown = TownDataStorage.get(player);

        if(PlayerDataStorage.get(player).getTownId() == null){
            event.setCancelled(true);
            return;
        }


        //Same town
        if(ClaimedChunkStorage.getChunkOwnerID(chunk).equals(playerTown.getID()))
            return;
        //Same alliance
        if(chunkTown.getChunkSettings().getBreakAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getID()))
            return;
        //permission is on foreign
        if(chunkTown.getChunkSettings().getBreakAuth() == TownChunkPermission.FOREIGN)
            return;
        //war has been declared
        if(WarTaggedPlayer.isPlayerInWarWithTown(player,chunkTown))
            return;

        playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(chunk));
        event.setCancelled(true);
    }

    @EventHandler
    public void OnContainersOpen(PlayerInteractEvent event){

        Block block = event.getClickedBlock();
        if (block != null){

            BlockData blockData = block.getBlockData();

            Chunk chunk = block.getLocation().getChunk();
            Player player = event.getPlayer();

            if(!ClaimedChunkStorage.isChunkClaimed(chunk))
                return;

            if(PlayerDataStorage.get(player).getTownId() == null){
                event.setCancelled(true);
                return;
            }
            if(blockData.getMaterial() == Material.CHEST||
                    blockData.getMaterial() == Material.TRAPPED_CHEST){


                TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));
                TownData playerTown = TownDataStorage.get(player);

                //Same town
                if(ClaimedChunkStorage.getChunkOwnerID(chunk).equals(playerTown.getID()))
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
            else if(blockData.getMaterial() == Material.OAK_DOOR ||
                    blockData.getMaterial() == Material.SPRUCE_DOOR ||
                    blockData.getMaterial() == Material.ACACIA_DOOR ||
                    blockData.getMaterial() == Material.DARK_OAK_DOOR ||
                    blockData.getMaterial() == Material.BAMBOO_DOOR ||
                    blockData.getMaterial() == Material.BIRCH_DOOR ||
                    blockData.getMaterial() == Material.CRIMSON_DOOR ||
                    blockData.getMaterial() == Material.JUNGLE_DOOR ||
                    blockData.getMaterial() == Material.WARPED_DOOR ||
                    blockData.getMaterial() == Material.IRON_DOOR){

                TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));
                TownData playerTown = TownDataStorage.get(player);

                //Same town
                if(ClaimedChunkStorage.getChunkOwnerID(chunk).equals(playerTown.getID()))
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
        if(PlayerDataStorage.get(event.getPlayer()).getTownId() == null){
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();

        TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));
        TownData playerTown = TownDataStorage.get(player);

        //Same town
        if(ClaimedChunkStorage.getChunkOwnerID(chunk).equals(playerTown.getID()))
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
