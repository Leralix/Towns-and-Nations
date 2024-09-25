package org.tan.TownsAndNations.listeners;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;

public class AttackListener implements Listener {


    @EventHandler
    public void onPlayerKilled(PlayerDeathEvent e) {
        Player killed = e.getEntity();
        Player killer = e.getEntity().getKiller();
        if(killer == null) {
            return;
        }
        PlayerDataStorage.get(killed).notifyDeathToAttacks();
    }
}
