package org.leralix.tan.listeners;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class AttackListener implements Listener {

    private final PlayerDataStorage playerDataStorage;

    public AttackListener(PlayerDataStorage playerDataStorage) {
        this.playerDataStorage = playerDataStorage;
    }

    @EventHandler
    public void onPlayerKilled(PlayerDeathEvent e) {
        Player killed = e.getEntity();

        ITanPlayer tanPlayer = playerDataStorage.get(killed);
        CurrentAttacksStorage.notifyPlayerDeath(tanPlayer);

    }
}