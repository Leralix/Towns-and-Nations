package org.tan.towns_and_nations.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.enums.TownChunkPermission;
import org.tan.towns_and_nations.enums.TownRelation;
import org.tan.towns_and_nations.storage.ClaimedChunkStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;

import static org.tan.towns_and_nations.utils.ChatUtils.getTANString;

public class ChunkListener implements Listener {

    @EventHandler
    public void OnBlockBreak(BlockBreakEvent event){

        Chunk chunk = event.getBlock().getLocation().getChunk();

        if(!ClaimedChunkStorage.isChunkClaimed(chunk))
            return;
        Player player = event.getPlayer();

        TownDataClass chunkTown = TownDataStorage.getTown(ClaimedChunkStorage.getChunkOwner(chunk));
        TownDataClass playerTown = TownDataStorage.getTown(player);

        //Same town
        if(ClaimedChunkStorage.getChunkOwner(chunk).equals(playerTown.getTownId()))
            return;
        //Same alliance
        if(chunkTown.getChunkSettings().getBreakAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getTownId()))
            return;
        //permission is on foreign
        if(chunkTown.getChunkSettings().getBreakAuth() == TownChunkPermission.FOREIGN)
            return;

        player.sendMessage(getTANString() + "This chunk belongs to " + ChatColor.GREEN + ClaimedChunkStorage.getChunkOwnerName(chunk));
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


                TownDataClass chunkTown = TownDataStorage.getTown(ClaimedChunkStorage.getChunkOwner(chunk));
                TownDataClass playerTown = TownDataStorage.getTown(player);

                //Same town
                if(ClaimedChunkStorage.getChunkOwner(chunk).equals(playerTown.getTownId()))
                    return;
                //Same alliance
                if(chunkTown.getChunkSettings().getChestAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getTownId()))
                    return;
                //permission is on foreign
                if(chunkTown.getChunkSettings().getChestAuth() == TownChunkPermission.FOREIGN)
                    return;

                player.sendMessage(getTANString() + "This chunk belongs to " + ChatColor.GREEN + ClaimedChunkStorage.getChunkOwnerName(chunk));
                event.setCancelled(true);

            }
            else if(blockName.contains("DOOR")){

                TownDataClass chunkTown = TownDataStorage.getTown(ClaimedChunkStorage.getChunkOwner(chunk));
                TownDataClass playerTown = TownDataStorage.getTown(player);

                //Same town
                if(ClaimedChunkStorage.getChunkOwner(chunk).equals(playerTown.getTownId()))
                    return;
                //Same alliance
                if(chunkTown.getChunkSettings().getDoorAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getTownId()))
                    return;
                //permission is on foreign
                if(chunkTown.getChunkSettings().getDoorAuth() == TownChunkPermission.FOREIGN)
                    return;

                player.sendMessage(getTANString() + "This chunk belongs to " + ChatColor.GREEN + ClaimedChunkStorage.getChunkOwnerName(chunk));
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

        TownDataClass chunkTown = TownDataStorage.getTown(ClaimedChunkStorage.getChunkOwner(chunk));
        TownDataClass playerTown = TownDataStorage.getTown(player);

        //Same town
        if(ClaimedChunkStorage.getChunkOwner(chunk).equals(playerTown.getTownId()))
            return;
        //Same alliance
        if(chunkTown.getChunkSettings().getPlaceAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getTownId()))
            return;
        //Permission is on foreign
        if(chunkTown.getChunkSettings().getPlaceAuth() == TownChunkPermission.FOREIGN)
            return;

        player.sendMessage(getTANString() + "This chunk belongs to " + ChatColor.GREEN + ClaimedChunkStorage.getChunkOwnerName(chunk));
        event.setCancelled(true);
    }








}
