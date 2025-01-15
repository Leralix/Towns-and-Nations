package org.leralix.tan.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.MetadataValue;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TownClaimedChunk;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;

public class PropertySignListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock != null && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            if (clickedBlock.getType() == Material.OAK_SIGN || clickedBlock.getType() == Material.OAK_WALL_SIGN) {
                Sign sign = (Sign) clickedBlock.getState();
                if (sign.hasMetadata("propertySign")) {
                    event.setCancelled(true);
                    for (MetadataValue value : sign.getMetadata("propertySign")) {
                        String customData = value.asString();
                        String[] ids = customData.split("_");
                        PropertyData propertyData = TownDataStorage.get(ids[0]).getProperty(ids[1]);
                        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {


                            if(playerEmbargoWithTown(player, clickedBlock)){
                                player.sendMessage(Lang.NO_TRADE_ALLOWED_EMBARGO.get());
                                return;
                            }





                            if(propertyData.getOwnerID().equals(player.getUniqueId().toString())){
                                PlayerGUI.openPropertyManagerMenu(player, propertyData);
                            }else if(propertyData.isRented() && propertyData.getRenterID().equals(player.getUniqueId().toString())){
                                PlayerGUI.openPropertyManagerRentMenu(player, propertyData);
                            }
                            else {
                                if(propertyData.isForRent() || propertyData.isForSale()){
                                    PlayerGUI.openPropertyBuyMenu(player, propertyData);
                                }
                                else
                                    player.sendMessage(Lang.PROPERTY_NOT_FOR_SALE_OR_RENT.get());
                            }
                        } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                            propertyData.showBox(player);
                        }
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
