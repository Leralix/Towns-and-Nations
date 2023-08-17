package org.tan.towns_and_nations.Tasks;

import org.bukkit.scheduler.BukkitRunnable;
import org.tan.towns_and_nations.TownsAndNations;
import org.tan.towns_and_nations.storage.ClaimedChunkStorage;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;

public class SaveStats {




    public static void startSchedule() {
        new BukkitRunnable() {
            @Override
            public void run() {
                TownDataStorage.saveStats();
                ClaimedChunkStorage.saveStats();
                PlayerStatStorage.saveStats();
            }
        }.runTaskTimer(TownsAndNations.getPlugin(), 0L, 1200L);
    }


}
