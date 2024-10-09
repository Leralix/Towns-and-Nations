package org.leralix.tan.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.Lang.Lang;
import org.leralix.tan.storage.DataStorage.PlayerDataStorage;
import org.leralix.tan.storage.TeleportationRegister;
import org.leralix.tan.utils.ChatUtils;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

public class SpawnListener implements Listener {

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player player) {
            if(TeleportationRegister.isPlayerRegistered(player.getUniqueId().toString()) &&
                    !TeleportationRegister.getTeleportationData(player).isCancelled()) {

                if(ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("cancelTeleportOnDamage", true)) {
                    PlayerData playerData = PlayerDataStorage.get(player.getUniqueId().toString());
                    TeleportationRegister.getTeleportationData(playerData).setCancelled(true);
                    player.sendMessage(ChatUtils.getTANString() + Lang.TELEPORTATION_CANCELLED.get());
                }


            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(TeleportationRegister.isPlayerRegistered(player.getUniqueId().toString()) &&
                    !TeleportationRegister.getTeleportationData(player).isCancelled()) {

            Location locationFrom = event.getFrom();
            Location locationTo = event.getTo();

            //If player moves only his head
            if(locationFrom.getBlockX() == locationTo.getBlockX() && locationFrom.getBlockZ() == locationTo.getBlockZ()) {
                if(ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("cancelTeleportOnMoveHead", false)) {
                    cancelTeleportation(player);
                }
            }
            else{
                //If player moves to a different position
                if(ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("cancelTeleportOnMovePosition", true)) {
                    cancelTeleportation(player);
                }
            }

        }
    }


    public void cancelTeleportation(Player player) {
        PlayerData playerData = PlayerDataStorage.get(player.getUniqueId().toString());
        TeleportationRegister.getTeleportationData(playerData).setCancelled(true);
        player.sendMessage(ChatUtils.getTANString() + Lang.TELEPORTATION_CANCELLED.get());
    }
}
