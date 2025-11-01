package org.leralix.tan.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.utils.FoliaScheduler;
import org.leralix.tan.war.capture.CaptureManager;
import org.leralix.tan.war.cosmetic.ShowBoundaries;
import org.leralix.tan.war.legacy.CurrentAttack;


public class SecondTask {


    public void startScheduler() {
        FoliaScheduler.runTaskTimer(TownsAndNations.getPlugin(), () -> {
            for (CurrentAttack currentAttack : CurrentAttacksStorage.getAll()) {
                CaptureManager.getInstance().updateCapture(currentAttack.getAttackData());
            }
            for (Player player : Bukkit.getOnlinePlayers()){
                ShowBoundaries.display(player);
            }
        }, 1L, 20L);
    }
}
