package org.leralix.tan.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.utils.FoliaScheduler;
import org.leralix.tan.wars.capture.CaptureManager;
import org.leralix.tan.wars.cosmetic.ShowBoundaries;
import org.leralix.tan.wars.legacy.CurrentAttack;

public class SecondTask {

  public void startScheduler() {
    FoliaScheduler.runTaskTimer(
        TownsAndNations.getPlugin(),
        () -> {
          // Update capture status (global operation, OK for GlobalRegionScheduler)
          for (CurrentAttack currentAttack : CurrentAttacksStorage.getAll()) {
            CaptureManager.getInstance().updateCapture(currentAttack.getAttackData());
          }
          // For each player, schedule the display task on their specific entity scheduler
          // This ensures thread-safety on Folia where players may be on different regions
          for (Player player : Bukkit.getOnlinePlayers()) {
            FoliaScheduler.runEntityTask(
                TownsAndNations.getPlugin(),
                player,
                () -> {
                  ShowBoundaries.display(player);
                });
          }
        },
        1L,
        20L);
  }
}
