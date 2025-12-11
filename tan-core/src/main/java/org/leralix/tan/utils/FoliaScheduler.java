package org.leralix.tan.utils;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class FoliaScheduler {

  private FoliaScheduler() {
    throw new IllegalStateException("Utility class");
  }

  public static void runTask(Plugin plugin, Runnable task) {
    Bukkit.getGlobalRegionScheduler().run(plugin, (t) -> task.run());
  }

  public static ScheduledTask runTaskLater(Plugin plugin, Runnable task, long delay) {
    return Bukkit.getGlobalRegionScheduler().runDelayed(plugin, (t) -> task.run(), delay);
  }

  public static void runTaskTimer(Plugin plugin, Runnable task, long delay, long period) {
    Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, (t) -> task.run(), delay, period);
  }

  public static void runTaskAsynchronously(Plugin plugin, Runnable task) {
    Bukkit.getAsyncScheduler().runNow(plugin, (t) -> task.run());
  }

  public static void runTaskLaterAsynchronously(Plugin plugin, Runnable task, long delay) {
    long delayMs = delay * 50;
    Bukkit.getAsyncScheduler()
        .runDelayed(plugin, (t) -> task.run(), delayMs, TimeUnit.MILLISECONDS);
  }

  public static void runEntityTask(Plugin plugin, Entity entity, Runnable task) {
    entity.getScheduler().run(plugin, (t) -> task.run(), null);
  }

  public static void runEntityTaskLater(Plugin plugin, Entity entity, Runnable task, long delay) {
    entity.getScheduler().runDelayed(plugin, (t) -> task.run(), null, delay);
  }

  public static void runTaskAtLocation(Plugin plugin, Location location, Runnable task) {
    Bukkit.getRegionScheduler().run(plugin, location, (t) -> task.run());
  }

  public static void runTaskLaterAtLocation(
      Plugin plugin, Location location, Runnable task, long delay) {
    Bukkit.getRegionScheduler().runDelayed(plugin, location, (t) -> task.run(), delay);
  }
}
