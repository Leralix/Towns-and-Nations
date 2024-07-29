package org.tan.TownsAndNations.Tasks;

import org.bukkit.scheduler.BukkitRunnable;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.DataStorage.AttackDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.*;

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
                AttackDataStorage.save();
            }
        }.runTaskTimer(TownsAndNations.getPlugin(), 0L, 1200L);
    }

}
