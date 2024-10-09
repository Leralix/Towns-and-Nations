package org.leralix.tan.Tasks;

import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.storage.DataStorage.PlannedAttackStorage;
import org.leralix.tan.storage.DataStorage.*;

public class SaveStats {

    public static void startSchedule() {
        new BukkitRunnable() {
            @Override
            public void run() {
                RegionDataStorage.saveStats();
                TownDataStorage.saveStats();
                PlayerDataStorage.saveStats();
                NewClaimedChunkStorage.save();
                LandmarkStorage.save();
                PlannedAttackStorage.save();
            }
        }.runTaskTimer(TownsAndNations.getPlugin(), 0L, 1200L);
    }

}
