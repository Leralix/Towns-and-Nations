package org.leralix.tan.listeners;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class AttackListener implements Listener {


    @EventHandler
    public void onPlayerKilled(PlayerDeathEvent e) {
        Player killed = e.getEntity();
        Player killer = e.getEntity().getKiller();
        PlayerDataStorage.getInstance().get(killed).notifyDeath(killer);
    }
}
