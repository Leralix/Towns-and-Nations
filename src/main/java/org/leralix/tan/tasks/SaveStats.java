package org.leralix.tan.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.storage.impl.FortDataStorage;
import org.leralix.tan.storage.stored.*;

public class SaveStats {

    private SaveStats() {
        throw new IllegalStateException("Utility class");
    }

    public static void startSchedule() {
        new BukkitRunnable() {
            @Override
            public void run() {
                saveAll();
            }
        }.runTaskTimer(TownsAndNations.getPlugin(), 0L, 1200L);
    }

    public static void saveAll() {
        RegionDataStorage.getInstance().save();
        TownDataStorage.getInstance().save();
        PlayerDataStorage.getInstance().save();
        NewClaimedChunkStorage.getInstance().save();
        LandmarkStorage.getInstance().save();
        PlannedAttackStorage.getInstance().save();
        WarStorage.getInstance().save();
        FortDataStorage.getInstance().save();
    }

}
