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
            PlayerData playerData = PlayerDataStorage.get(player.getUniqueId().toString());
            if(SpawnRegister.isPlayerRegistered(playerData)) {
                if(ConfigUtil.getCustomConfig("config.yml").getBoolean("cancelTeleportOnDamage", true)) {
                    SpawnRegister.removePlayer(playerData);
                    player.sendMessage(ChatUtils.getTANString() + Lang.TELEPORTATION_CANCELLED.get());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerDataStorage.get(player.getUniqueId().toString());
        if(SpawnRegister.isPlayerRegistered(playerData)) {
            if(ConfigUtil.getCustomConfig("config.yml").getBoolean("cancelTeleportOnMove", true)) {
                SpawnRegister.removePlayer(playerData);
                player.sendMessage(ChatUtils.getTANString() + Lang.TELEPORTATION_CANCELLED.get());
            }
        }
    }
}
