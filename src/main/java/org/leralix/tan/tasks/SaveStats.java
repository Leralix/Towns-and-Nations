package org.leralix.tan.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.storage.stored.*;
import org.leralix.tan.war.WarStorage;

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
        RegionDataStorage.getInstance().saveStats();
        TownDataStorage.getInstance().saveStats();
        PlayerDataStorage.getInstance().saveStats();
        NewClaimedChunkStorage.getInstance().save();
        LandmarkStorage.getInstance().save();
        CurrentWarStorage.save();
        WarStorage.getInstance().save();
    }

}
