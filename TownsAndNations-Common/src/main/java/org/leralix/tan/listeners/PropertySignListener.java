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
import org.leralix.tan.data.building.property.PropertyData;
import org.leralix.tan.data.chunk.ClaimedChunk;
import org.leralix.tan.data.chunk.TownClaimedChunk;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.gui.user.RenterPropertyMenu;
import org.leralix.tan.gui.user.property.BuyOrRentPropertyMenu;
import org.leralix.tan.gui.user.property.PlayerPropertyManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.TanChatUtils;

public class PropertySignListener implements Listener {

    private static final String PROPERTY_SIGN_METADATA = "propertySign";

    private final PlayerDataStorage playerDataStorage;

    public PropertySignListener(PlayerDataStorage playerDataStorage) {
        this.playerDataStorage = playerDataStorage;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock != null &&
                (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) &&
                (clickedBlock.getType() == Material.OAK_SIGN || clickedBlock.getType() == Material.OAK_WALL_SIGN)) {

            Sign sign = (Sign) clickedBlock.getState();
            if (sign.hasMetadata(PROPERTY_SIGN_METADATA)) {
                event.setCancelled(true);
                for (MetadataValue value : sign.getMetadata(PROPERTY_SIGN_METADATA)) {
                    String customData = value.asString();
                    String[] ids = customData.split("_");
                    PropertyData propertyData = TownDataStorage.getInstance().get(ids[0]).getProperty(ids[1]);
                    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

                        ITanPlayer tanPlayer = playerDataStorage.get(player);
                        LangType langType = tanPlayer.getLang();

                        if (!canPlayerOpenMenu(player, clickedBlock)) {
                            TanChatUtils.message(player, Lang.NO_TRADE_ALLOWED_EMBARGO.get(langType));
                            return;
                        }
                        if (propertyData.getOwner().canAccess(tanPlayer)) {
                            new PlayerPropertyManager(player, propertyData, HumanEntity::closeInventory);
                        } else if (propertyData.isRented()
                                && propertyData.getRenterID().equals(player.getUniqueId().toString())) {
                            new RenterPropertyMenu(player, propertyData);
                        } else {
                            if (propertyData.isForRent() || propertyData.isForSale()) {
                                new BuyOrRentPropertyMenu(player, propertyData);
                            } else {
                                TanChatUtils.message(player, Lang.PROPERTY_NOT_FOR_SALE_OR_RENT.get(langType));
                            }
                        }
                    } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        propertyData.showBox(player);
                    }
                }
            }
        }

    }

    private boolean canPlayerOpenMenu(Player player, Block clickedBlock) {
        ClaimedChunk claimedChunk = NewClaimedChunkStorage.getInstance().get(clickedBlock.getChunk());
        ITanPlayer tanPlayer = playerDataStorage.get(player);
        if (tanPlayer.hasTown() && claimedChunk instanceof TownClaimedChunk townClaimedChunk) {
            TownRelation territoryRelation = townClaimedChunk.getTown().getWorstRelationWith(tanPlayer);
            return Constants.getRelationConstants(territoryRelation).canInteractWithProperty();
        }
        return false;
    }
}
