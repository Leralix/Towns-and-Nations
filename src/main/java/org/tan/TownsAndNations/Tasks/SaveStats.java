package org.tan.TownsAndNations.Tasks;

import org.bukkit.scheduler.BukkitRunnable;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.ClaimedChunkStorage;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;

public class SaveStats {




    public static void startSchedule() {
        new BukkitRunnable() {
            @Override
            public void run() {
                TownDataStorage.saveStats();
                ClaimedChunkStorage.saveStats();
                PlayerDataStorage.saveStats();
            }
        }.runTaskTimer(TownsAndNations.getPlugin(), 0L, 1200L);
    }


}
