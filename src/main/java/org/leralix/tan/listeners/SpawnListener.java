package org.leralix.tan.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.storage.TeleportationRegister;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.lang.Lang;

public class SpawnListener implements Listener {

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player player) {
            if(TeleportationRegister.isPlayerRegistered(player.getUniqueId().toString()) &&
                    !TeleportationRegister.getTeleportationData(player).isCancelled()) {

                if(ConfigUtil.getCustomConfig(ConfigTag.TAN).getBoolean("cancelTeleportOnDamage", true)) {
                    PlayerData playerData = PlayerDataStorage.get(player.getUniqueId().toString());
                    TeleportationRegister.getTeleportationData(playerData).setCancelled(true);
                    player.sendMessage(TanChatUtils.getTANString() + Lang.TELEPORTATION_CANCELLED.get());
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
                if(ConfigUtil.getCustomConfig(ConfigTag.TAN).getBoolean("cancelTeleportOnMoveHead", false)) {
                    cancelTeleportation(player);
                }
            }
            else{
                //If player moves to a different position
                if(ConfigUtil.getCustomConfig(ConfigTag.TAN).getBoolean("cancelTeleportOnMovePosition", true)) {
                    cancelTeleportation(player);
                }
            }

        }
    }


    public void cancelTeleportation(Player player) {
        PlayerData playerData = PlayerDataStorage.get(player.getUniqueId().toString());
        TeleportationRegister.getTeleportationData(playerData).setCancelled(true);
        player.sendMessage(TanChatUtils.getTANString() + Lang.TELEPORTATION_CANCELLED.get());
    }
}
