package org.tan.TownsAndNations.Tasks;

import org.bukkit.scheduler.BukkitRunnable;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.ClaimedChunkStorage;
import org.tan.TownsAndNations.storage.PlayerStatStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;

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
