package org.leralix.tan.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.storage.stored.json.PremiumStorage;
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
        plugin.getNationStorage().save();
        plugin.getRegionStorage().save();
        plugin.getTownStorage().save();
        plugin.getPlayerDataStorage().save();
        plugin.getClaimStorage().save();
        plugin.getLandmarkStorage().save();
        plugin.getWarStorage().save();
        plugin.getFortStorage().save();
        PremiumStorage.getInstance().save();
        TruceStorage.getInstance().save();
    }

}
