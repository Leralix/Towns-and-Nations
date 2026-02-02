package org.leralix.tan.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.TeleportationRegister;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.TanChatUtils;

public class SpawnListener implements Listener {

    private final PlayerDataStorage playerDataStorage;

    public SpawnListener(PlayerDataStorage playerDataStorage) {
        this.playerDataStorage = playerDataStorage;
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player &&
                TeleportationRegister.isPlayerRegistered(player.getUniqueId()) &&
                Constants.isCancelTeleportOnDamage() &&
                !TeleportationRegister.getTeleportationData(player).isCancelled()) {
            ITanPlayer tanPlayer = playerDataStorage.get(player.getUniqueId().toString());
            TeleportationRegister.getTeleportationData(tanPlayer).setCancelled(true);
            TanChatUtils.message(player, Lang.TELEPORTATION_CANCELLED.get(tanPlayer));
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (TeleportationRegister.isPlayerRegistered(player.getUniqueId()) &&
                !TeleportationRegister.getTeleportationData(player).isCancelled()) {

            Location locationFrom = event.getFrom();
            Location locationTo = event.getTo();

            // If player moves only his head
            if (locationFrom.getBlockX() == locationTo.getBlockX()
                    && locationFrom.getBlockZ() == locationTo.getBlockZ()) {
                if (Constants.isCancelTeleportOnMoveHead()) {
                    cancelTeleportation(player);
                }
            } else {
                // If player moves to a different position
                if (Constants.isCancelTeleportOnMovePosition()) {
                    cancelTeleportation(player);
                }
            }

        }
    }

    public void cancelTeleportation(Player player) {
        ITanPlayer tanPlayer = playerDataStorage.get(player.getUniqueId().toString());
        TeleportationRegister.getTeleportationData(tanPlayer).setCancelled(true);
        TanChatUtils.message(player, Lang.TELEPORTATION_CANCELLED.get(tanPlayer));
    }
}
