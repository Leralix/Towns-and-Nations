package org.leralix.tan.utils;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;

/**
 * Utility class to handle Folia scheduler compatibility
 * This class provides methods to schedule tasks in a Folia-compatible way
 */
public class FoliaScheduler {

    private FoliaScheduler() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Run a task on the global region scheduler
     * Use this for tasks that don't depend on a specific location or entity
     *
     * @param plugin The plugin
     * @param task   The task to run
     */
    public static void runTask(Plugin plugin, Runnable task) {
        Bukkit.getGlobalRegionScheduler().run(plugin, (t) -> task.run());
    }

    /**
     * Run a task later on the global region scheduler
     *
     * @param plugin The plugin
     * @param task   The task to run
     * @param delay  The delay in ticks
     * @return The scheduled task
     */
    public static ScheduledTask runTaskLater(Plugin plugin, Runnable task, long delay) {
        return Bukkit.getGlobalRegionScheduler().runDelayed(plugin, (t) -> task.run(), delay);
    }

    /**
     * Run a repeating task on the global region scheduler
     *
     * @param plugin The plugin
     * @param task   The task to run
     * @param delay  The initial delay in ticks
     * @param period The period between runs in ticks
     */
    public static void runTaskTimer(Plugin plugin, Runnable task, long delay, long period) {
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, (t) -> task.run(), delay, period);
    }

    /**
     * Run an async task
     *
     * @param plugin The plugin
     * @param task   The task to run
     */
    public static void runTaskAsynchronously(Plugin plugin, Runnable task) {
        Bukkit.getAsyncScheduler().runNow(plugin, (t) -> task.run());
    }

    /**
     * Run an async task later
     *
     * @param plugin The plugin
     * @param task   The task to run
     * @param delay  The delay in ticks
     */
    public static void runTaskLaterAsynchronously(Plugin plugin, Runnable task, long delay) {
        long delayMs = delay * 50; // Convert ticks to milliseconds
        Bukkit.getAsyncScheduler().runDelayed(plugin, (t) -> task.run(), delayMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Run a task on the entity's scheduler
     * Use this for tasks that involve a specific entity
     *
     * @param plugin The plugin
     * @param entity The entity
     * @param task   The task to run
     */
    public static void runEntityTask(Plugin plugin, Entity entity, Runnable task) {
        entity.getScheduler().run(plugin, (t) -> task.run(), null);
    }

    /**
     * Run a task later on the entity's scheduler
     *
     * @param plugin The plugin
     * @param entity The entity
     * @param task   The task to run
     * @param delay  The delay in ticks
     */
    public static void runEntityTaskLater(Plugin plugin, Entity entity, Runnable task, long delay) {
        entity.getScheduler().runDelayed(plugin, (t) -> task.run(), null, delay);
    }

    /**
     * Run a task on a region scheduler
     * Use this for tasks that involve a specific location
     *
     * @param plugin   The plugin
     * @param location The location
     * @param task     The task to run
     */
    public static void runTaskAtLocation(Plugin plugin, Location location, Runnable task) {
        Bukkit.getRegionScheduler().run(plugin, location, (t) -> task.run());
    }

    /**
     * Run a task later on a region scheduler
     *
     * @param plugin   The plugin
     * @param location The location
     * @param task     The task to run
     * @param delay    The delay in ticks
     */
    public static void runTaskLaterAtLocation(Plugin plugin, Location location, Runnable task, long delay) {
        Bukkit.getRegionScheduler().runDelayed(plugin, location, (t) -> task.run(), delay);
    }
}
