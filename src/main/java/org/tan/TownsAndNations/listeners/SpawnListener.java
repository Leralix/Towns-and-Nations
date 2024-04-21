package org.tan.TownsAndNations.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.SpawnRegister;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.ConfigUtil;

public class SpawnListener implements Listener {

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player player) {
            if(SpawnRegister.isPlayerRegistered(player.getUniqueId().toString()) &&
                    !SpawnRegister.getTeleportationData(player).isCancelled()) {

                if(ConfigUtil.getCustomConfig("config.yml").getBoolean("cancelTeleportOnDamage", true)) {
                    PlayerData playerData = PlayerDataStorage.get(player.getUniqueId().toString());
                    SpawnRegister.getTeleportationData(playerData).setCancelled(true);
                    player.sendMessage(ChatUtils.getTANString() + Lang.TELEPORTATION_CANCELLED.get());
                }


            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(SpawnRegister.isPlayerRegistered(player.getUniqueId().toString())&&
                    !SpawnRegister.getTeleportationData(player).isCancelled()) {

            if(ConfigUtil.getCustomConfig("config.yml").getBoolean("cancelTeleportOnMove", true)) {
                PlayerData playerData = PlayerDataStorage.get(player.getUniqueId().toString());
                SpawnRegister.getTeleportationData(playerData).setCancelled(true);
                player.sendMessage(ChatUtils.getTANString() + Lang.TELEPORTATION_CANCELLED.get());
            }
        }
    }
}
