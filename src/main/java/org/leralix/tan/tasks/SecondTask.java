package org.leralix.tan.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.wars.CurrentAttack;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.war.capture.CaptureManager;


public class SecondTask {


    public void startScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (CurrentAttack currentAttack : CurrentAttacksStorage.getAll()) {
                    CaptureManager.getInstance().updateCapture(currentAttack.getAttackData());
                    currentAttack.displayBoundaries();
                }
            }
        }.runTaskTimer(TownsAndNations.getPlugin(), 0L, 20L); // Exécute toutes les 1200 ticks (1 minute en temps Minecraft)
    }
}
