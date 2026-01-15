package org.leralix.tan.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.storage.stored.*;
import org.leralix.tan.utils.constants.Constants;

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
        if (Constants.enableNation()) {
            NationDataStorage.getInstance().save();
        }
        PlayerDataStorage.getInstance().save();
        NewClaimedChunkStorage.getInstance().save();
        LandmarkStorage.getInstance().save();
        WarStorage.getInstance().save();
        FortStorage.getInstance().save();
        PremiumStorage.getInstance().save();
        TruceStorage.getInstance().save();
    }

}
