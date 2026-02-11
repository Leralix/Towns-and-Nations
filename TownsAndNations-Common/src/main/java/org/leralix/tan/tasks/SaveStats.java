package org.leralix.tan.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.storage.stored.*;
import org.leralix.tan.storage.stored.truce.TruceStorage;

public class SaveStats {

    private final TownsAndNations plugin;

    public SaveStats(TownsAndNations plugin) {
        this.plugin = plugin;
    }

    public void startSchedule() {
        new BukkitRunnable() {
            @Override
            public void run() {
                saveAll();
            }
        }.runTaskTimer(TownsAndNations.getPlugin(), 0L, 1200L);
    }

    public void saveAll() {
        NationDataStorage.getInstance().save();
        RegionDataStorage.getInstance().save();
        plugin.getTownDataStorage().save();
        plugin.getPlayerDataStorage().save();
        NewClaimedChunkStorage.getInstance().save();
        LandmarkStorage.getInstance().save();
        WarStorage.getInstance().save();
        FortStorage.getInstance().save();
        PremiumStorage.getInstance().save();
        TruceStorage.getInstance().save();
    }

}
