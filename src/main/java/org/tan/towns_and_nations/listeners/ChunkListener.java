package org.tan.towns_and_nations.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.storage.ClaimedChunkStorage;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;

import static org.tan.towns_and_nations.utils.ChatUtils.getTANString;

public class ChunkListener implements Listener {

    @EventHandler
    public void OnBlockBreak(BlockBreakEvent event){

        Chunk chunk = event.getBlock().getLocation().getChunk();

        if(!ClaimedChunkStorage.isChunkClaimed(chunk))
            return;
        Player player = event.getPlayer();
        TownDataClass town = TownDataStorage.getTown(player);
        if(!ClaimedChunkStorage.getChunkOwner(chunk).equals(town.getTownId())){
            player.sendMessage(getTANString() + "This chunk belongs to " + ChatColor.GREEN + ClaimedChunkStorage.getChunkOwnerName(chunk));
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void OnCointainersOpen(PlayerInteractEvent event){

        Block block = event.getClickedBlock();
        if (block != null){
            String blockName = block.getType().name();
            if(blockName.equals("CHEST")){

                Player player = event.getPlayer();
                Chunk chunk = block.getLocation().getChunk();
                TownDataClass town = TownDataStorage.getTown(player);

                if(!ClaimedChunkStorage.getChunkOwner(chunk).equals(town.getTownId())){
                    player.sendMessage(getTANString() + "This chunk belongs to " + ChatColor.GREEN + ClaimedChunkStorage.getChunkOwnerName(chunk));
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void OnBlocPlaced(BlockPlaceEvent event){

        Block block = event.getBlock();
        String blockName = block.getType().name();
        if(blockName.equals("CHEST")){

            Player player = event.getPlayer();
            Chunk chunk = block.getLocation().getChunk();
            TownDataClass town = TownDataStorage.getTown(player);

            if(!ClaimedChunkStorage.getChunkOwner(chunk).equals(town.getTownId())){
                player.sendMessage(getTANString() + "This chunk belongs to " + ChatColor.GREEN + ClaimedChunkStorage.getChunkOwnerName(chunk));
                event.setCancelled(true);
            }
        }
    }








}
