package org.leralix.tan.tasks;

import org.leralix.tan.TownsAndNations;
import org.leralix.tan.storage.stored.*;
import org.leralix.tan.utils.FoliaScheduler;

public class SaveStats {

    private SaveStats() {
        throw new IllegalStateException("Utility class");
    }

    public static void startSchedule() {
        FoliaScheduler.runTaskTimer(TownsAndNations.getPlugin(), SaveStats::saveAll, 1L, 1200L);
    }

    public static void saveAll() {
        RegionDataStorage.getInstance().save();
        TownDataStorage.getInstance().save();
        PlayerDataStorage.getInstance().save();
        NewClaimedChunkStorage.getInstance().save();
        LandmarkStorage.getInstance().save();
        PlannedAttackStorage.getInstance().save();
        WarStorage.getInstance().save();
        FortStorage.getInstance().save();
        PremiumStorage.getInstance().save();
        TruceStorage.getInstance().save();
    }

}
