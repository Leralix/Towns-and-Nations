package org.leralix.tan.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.storage.stored.*;

public class SaveStats {

    private SaveStats() {
        throw new IllegalStateException("Utility class");
    }

    public static void startSchedule() {
        new BukkitRunnable() {
            @Override
            public void run() {
                RegionDataStorage.saveStats();
                TownDataStorage.saveStats();
                PlayerDataStorage.getInstance().saveStats();
                NewClaimedChunkStorage.getInstance().save();
                LandmarkStorage.save();
                PlannedAttackStorage.save();
            }
        }.runTaskTimer(TownsAndNations.getPlugin(), 0L, 1200L);
    }

}
