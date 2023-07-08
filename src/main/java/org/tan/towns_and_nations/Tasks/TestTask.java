package org.tan.towns_and_nations.Tasks;

import org.bukkit.scheduler.BukkitRunnable;
import org.tan.towns_and_nations.TownsAndNations;
import org.tan.towns_and_nations.storage.ClaimedChunkStorage;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TestTask {




    public static void startSchedule() {
        new BukkitRunnable() {
            @Override
            public void run() {
                TownsAndNations.getPluginLogger().info("Sauvegardes des donnés");
                TownDataStorage.saveStats();
                ClaimedChunkStorage.saveStats();
                PlayerStatStorage.saveStats();
            }
        }.runTaskTimer(TownsAndNations.getPlugin(), 0L, 1200L); // Exécute toutes les 1200 ticks (1 minute en temps Minecraft)
    }


}
