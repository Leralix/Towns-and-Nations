package org.leralix.tan.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.war.capture.CaptureManager;
import org.leralix.tan.war.cosmetic.ShowBoundaries;
import org.leralix.tan.war.legacy.CurrentAttack;


public class SecondTask {


    public void startScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (CurrentAttack currentAttack : CurrentAttacksStorage.getAll()) {
                    CaptureManager.getInstance().updateCapture(currentAttack.getAttackData());
                }
                for (Player player : Bukkit.getOnlinePlayers()){
                    ShowBoundaries.display(player);
                }

            }
        }.runTaskTimer(TownsAndNations.getPlugin(), 0L, 20L);
    }
}
