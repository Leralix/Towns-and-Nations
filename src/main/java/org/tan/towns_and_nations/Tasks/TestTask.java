package org.tan.towns_and_nations.Tasks;

import org.bukkit.scheduler.BukkitRunnable;
import org.tan.towns_and_nations.TownsAndNations;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TestTask {




    public static void startSchedule() {
        new BukkitRunnable() {
            @Override
            public void run() {
                TownsAndNations.getPluginLogger().info("Ce texte est censé apparaitre chaque minute");
            }
        }.runTaskTimer(TownsAndNations.getPlugin(), 0L, 1200L); // Exécute toutes les 1200 ticks (1 minute en temps Minecraft)
    }


}
