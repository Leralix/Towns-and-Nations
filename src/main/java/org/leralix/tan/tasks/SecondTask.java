package org.leralix.tan.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.wars.CurrentAttack;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.war.capture.CaptureManager;


public class SecondTask {

    CaptureManager captureManager;

    public SecondTask() {
        captureManager = new CaptureManager();
    }

    public void startScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (CurrentAttack currentAttack : CurrentAttacksStorage.getAll()) {
                    captureManager.updateCapture(currentAttack);
                }
            }
        }.runTaskTimer(TownsAndNations.getPlugin(), 0L, 20L); // Exécute toutes les 1200 ticks (1 minute en temps Minecraft)
    }
}
