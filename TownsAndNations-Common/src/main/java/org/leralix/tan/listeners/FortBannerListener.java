package org.leralix.tan.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.MetadataValue;
import org.leralix.tan.data.building.fort.Fort;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.FortStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;

import java.util.List;

public class FortBannerListener implements Listener {

    private static final String FORT_FLAG_METADATA = "fortFlag";

    private final PlayerDataStorage playerDataStorage;
    private final FortStorage fortStorage;

    public FortBannerListener(PlayerDataStorage playerDataStorage, FortStorage fortStorage) {
        this.playerDataStorage = playerDataStorage;
        this.fortStorage = fortStorage;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) {
            return;
        }

        if (clickedBlock.hasMetadata(FORT_FLAG_METADATA) && Tag.BANNERS.isTagged(clickedBlock.getType())) {
            List<MetadataValue> metadatas = clickedBlock.getMetadata(FORT_FLAG_METADATA);
            for (MetadataValue metadataValue : metadatas) {
                Fort fort = fortStorage.getFort(metadataValue.asString());
                if (fort != null) {
                    event.setCancelled(true);
                    ITanPlayer playerData = playerDataStorage.get(player);
                    player.sendActionBar(
                            Component.text(
                                Lang.FORT_ACTION_BAR_INFO.get(playerData.getLang(), fort.getName(), fort.getOccupier().getColoredName())
                            )
                    );
                    return;
                }

            }
        }
    }

}
