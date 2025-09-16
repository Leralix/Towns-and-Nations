package org.leralix.tan.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.MetadataValue;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TownClaimedChunk;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.gui.user.RenterPropertyMenu;
import org.leralix.tan.gui.user.property.BuyOrRentPropertyMenu;
import org.leralix.tan.gui.user.property.PlayerPropertyManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.constants.Constants;

public class PropertySignListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock != null &&
                (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) &&
                (clickedBlock.getType() == Material.OAK_SIGN || clickedBlock.getType() == Material.OAK_WALL_SIGN)) {

                Sign sign = (Sign) clickedBlock.getState();
                if (sign.hasMetadata("propertySign")) {
                    event.setCancelled(true);
                    for (MetadataValue value : sign.getMetadata("propertySign")) {
                        String customData = value.asString();
                        String[] ids = customData.split("_");
                        PropertyData propertyData = TownDataStorage.getInstance().get(ids[0]).getProperty(ids[1]);
                        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

                            if(!canPlayerOpenMenu(player, clickedBlock)){
                                player.sendMessage(Lang.NO_TRADE_ALLOWED_EMBARGO.get());
                                return;
                            }

                            if(propertyData.getOwnerID().equals(player.getUniqueId().toString())){
                                new PlayerPropertyManager(player, propertyData, HumanEntity::closeInventory);
                            }else if(propertyData.isRented() && propertyData.getRenterID().equals(player.getUniqueId().toString())){
                                new RenterPropertyMenu(player, propertyData);
                            }
                            else {
                                if(propertyData.isForRent() || propertyData.isForSale()){
                                    new BuyOrRentPropertyMenu(player, propertyData);
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

    private boolean canPlayerOpenMenu(Player player, Block clickedBlock) {
        ClaimedChunk2 claimedChunk2 = NewClaimedChunkStorage.getInstance().get(clickedBlock.getChunk());
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        if(tanPlayer.hasTown() && claimedChunk2 instanceof TownClaimedChunk townClaimedChunk){
            TownRelation territoryRelation = townClaimedChunk.getTown().getWorstRelationWith(tanPlayer);
            return Constants.getRelationConstants(territoryRelation).canInteractWithProperty();
        }
        return false;
    }
}
