package org.leralix.tan.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.storage.MinimapManager;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.war.capture.CaptureManager;
import org.leralix.tan.war.cosmetic.BoundaryRegister;
import org.leralix.tan.war.cosmetic.ShowBoundaries;

/**
 * This class run tasks that need to be executed every second.
 */
public class SecondTask {

    private final PlayerDataStorage playerDataStorage;
    private final MinimapManager minimapManager;

    public SecondTask(PlayerDataStorage playerDataStorage, MinimapManager minimapManager){
        this.playerDataStorage = playerDataStorage;
        this.minimapManager = minimapManager;
    }

    public void startScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {

                CaptureManager.getInstance().updateCapture();

                for (Player player : Bukkit.getOnlinePlayers()){
                    if(BoundaryRegister.isRegistered(player)){
                        ShowBoundaries.display(player, playerDataStorage.get(player));
                    }
                }
                minimapManager.displayMap();

            }
        }.runTaskTimer(TownsAndNations.getPlugin(), 0L, 20L);
    }
}
