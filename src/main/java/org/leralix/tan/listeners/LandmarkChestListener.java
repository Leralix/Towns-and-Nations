package org.leralix.tan.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.MetadataValue;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TownClaimedChunk;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.Lang.Lang;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.storage.DataStorage.LandmarkStorage;
import org.leralix.tan.storage.DataStorage.NewClaimedChunkStorage;
import org.leralix.tan.storage.DataStorage.PlayerDataStorage;

public class LandmarkChestListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock != null && (event.getAction() == Action.RIGHT_CLICK_BLOCK)){
            if (clickedBlock.getType() == Material.CHEST) {


                if(clickedBlock.hasMetadata("LandmarkChest")){
                    event.setCancelled(true);
                    PlayerData playerData = PlayerDataStorage.get(player);
                    for (MetadataValue value : clickedBlock.getMetadata("LandmarkChest")) {
                        String customData = value.asString();
                        Landmark landmark = LandmarkStorage.get(customData);
                        if(!playerData.haveTown()){
                            player.sendMessage(Lang.PLAYER_NO_TOWN.get());
                            return;
                        }
                        PlayerGUI.dispatchLandmarkGui(player, landmark);
                    }
                }
            }
        }
    }

    private boolean playerEmbargoWithTown(Player player, Block clickedBlock) {
        ClaimedChunk2 claimedChunk2 = NewClaimedChunkStorage.get(clickedBlock.getChunk());
        PlayerData playerData = PlayerDataStorage.get(player);
        if(claimedChunk2 != null &&
                playerData.haveTown() &&
                claimedChunk2 instanceof TownClaimedChunk townClaimedChunk){

            TownRelation townRelation = townClaimedChunk.getTown().getRelationWith(playerData.getTown());

            return townRelation == TownRelation.EMBARGO || townRelation == TownRelation.WAR;
        }
        return false;
    }
}
