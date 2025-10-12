package org.leralix.tan.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.MetadataValue;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

public class LandmarkChestListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock != null &&
                (event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                clickedBlock.getType() == Material.CHEST &&
                clickedBlock.hasMetadata("LandmarkChest")) {
            event.setCancelled(true);
            ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
            for (MetadataValue value : clickedBlock.getMetadata("LandmarkChest")) {
                String customData = value.asString();
                Landmark landmark = LandmarkStorage.getInstance().get(customData);
                if (!tanPlayer.hasTown()) {
                    TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(tanPlayer.getLang()));
                    return;
                }
                PlayerGUI.dispatchLandmarkGui(player, landmark);
            }
        }


    }

}
