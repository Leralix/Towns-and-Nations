package org.leralix.tan.utils.gui;

import dev.triumphteam.gui.guis.Gui;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.FoliaScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for creating asynchronous GUI loading patterns.
 *
 * <p>This helper prevents blocking the main thread during GUI data loading by:
 *
 * <ul>
 *   <li>Showing immediate loading screen with cached data
 *   <li>Loading data asynchronously
 *   <li>Refreshing GUI on main thread when data is ready
 * </ul>
 *
 * <p><b>Usage Example:</b>
 *
 * <pre>{@code
 * public class MyMenu extends BasicGui {
 *   private List<GuiItem> cachedItems = new ArrayList<>();
 *   private boolean isLoaded = false;
 *
 *   @Override
 *   public void open() {
 *     // Show immediate loading screen
 *     renderGui(cachedItems);
 *     gui.open(player);
 *
 *     // Load data asynchronously if not already loaded
 *     if (!isLoaded) {
 *       AsyncGuiHelper.loadAsync(
 *         player,
 *         () -> loadDataFromDatabase(), // Async supplier
 *         items -> {                    // Main thread consumer
 *           cachedItems = items;
 *           isLoaded = true;
 *           renderGui(items);
 *           gui.update();
 *         }
 *       );
 *     }
 *   }
 * }
 * }</pre>
 */
public class AsyncGuiHelper {

  private static final Logger logger = LoggerFactory.getLogger(AsyncGuiHelper.class);

  private AsyncGuiHelper() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Loads data asynchronously and updates the GUI on the main thread.
   *
   * @param <T> The type of data to load
   * @param player The player viewing the GUI
   * @param asyncLoader Supplier that loads data on async thread (can do blocking operations)
   * @param mainThreadConsumer Consumer that handles loaded data on main thread (updates GUI)
   */
  public static <T> void loadAsync(
      Player player, Supplier<T> asyncLoader, Consumer<T> mainThreadConsumer) {
    FoliaScheduler.runTaskAsynchronously(
        TownsAndNations.getPlugin(),
        () -> {
          try {
            // Load data on async thread - safe to do blocking operations here
            T data = asyncLoader.get();

            // Update GUI on main thread
            FoliaScheduler.runTask(
                TownsAndNations.getPlugin(),
                () -> {
                  if (player.isOnline()) {
                    try {
                      mainThreadConsumer.accept(data);
                    } catch (Exception e) {
                      logger.error(
                          "Error updating GUI for player {} on main thread", player.getName(), e);
                    }
                  }
                });
          } catch (Exception e) {
            logger.error("Error loading GUI data for player {}", player.getName(), e);
          }
        });
  }

  /**
   * Loads data asynchronously with error handling and a fallback value.
   *
   * @param <T> The type of data to load
   * @param player The player viewing the GUI
   * @param asyncLoader Supplier that loads data on async thread
   * @param mainThreadConsumer Consumer that handles loaded data on main thread
   * @param fallbackValue Value to use if loading fails
   */
  public static <T> void loadAsyncWithFallback(
      Player player, Supplier<T> asyncLoader, Consumer<T> mainThreadConsumer, T fallbackValue) {
    FoliaScheduler.runTaskAsynchronously(
        TownsAndNations.getPlugin(),
        () -> {
          T data;
          try {
            data = asyncLoader.get();
          } catch (Exception e) {
            logger.error(
                "Error loading GUI data for player {}, using fallback", player.getName(), e);
            data = fallbackValue;
          }

          final T finalData = data;
          FoliaScheduler.runTask(
              TownsAndNations.getPlugin(),
              () -> {
                if (player.isOnline()) {
                  try {
                    mainThreadConsumer.accept(finalData);
                  } catch (Exception e) {
                    logger.error(
                        "Error updating GUI for player {} on main thread", player.getName(), e);
                  }
                }
              });
        });
  }

  /**
   * Pre-fetches player data asynchronously before creating a GUI.
   *
   * <p>This is useful for GUIs that need ITanPlayer data but don't want to block the main thread
   * with getSync() calls.
   *
   * @param player The player to fetch data for
   * @param guiCreator Consumer that creates and opens the GUI with the fetched data
   */
  public static void prefetchPlayerData(Player player, Consumer<ITanPlayer> guiCreator) {
    FoliaScheduler.runTaskAsynchronously(
        TownsAndNations.getPlugin(),
        () -> {
          try {
            ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);

            FoliaScheduler.runTask(
                TownsAndNations.getPlugin(),
                () -> {
                  if (player.isOnline() && tanPlayer != null) {
                    try {
                      guiCreator.accept(tanPlayer);
                    } catch (Exception e) {
                      logger.error(
                          "Error creating GUI for player {} with prefetched data",
                          player.getName(),
                          e);
                    }
                  }
                });
          } catch (Exception e) {
            logger.error("Error prefetching player data for {}", player.getName(), e);
          }
        });
  }

  /**
   * Creates a CompletableFuture for async GUI data loading.
   *
   * <p>This is useful when you need more control over the async flow, such as chaining multiple
   * async operations.
   *
   * @param <T> The type of data to load
   * @param player The player viewing the GUI
   * @param asyncLoader Supplier that loads data on async thread
   * @return CompletableFuture that will complete with the loaded data
   */
  public static <T> CompletableFuture<T> loadAsyncFuture(Player player, Supplier<T> asyncLoader) {
    CompletableFuture<T> future = new CompletableFuture<>();

    FoliaScheduler.runTaskAsynchronously(
        TownsAndNations.getPlugin(),
        () -> {
          try {
            T data = asyncLoader.get();
            future.complete(data);
          } catch (Exception e) {
            logger.error("Error loading GUI data for player {}", player.getName(), e);
            future.completeExceptionally(e);
          }
        });

    return future;
  }

  /**
   * Updates a GUI component asynchronously and refreshes it on the main thread.
   *
   * @param gui The GUI to update
   * @param player The player viewing the GUI
   * @param updateAction Action to perform on main thread to update the GUI
   */
  public static void refreshGui(Gui gui, Player player, Runnable updateAction) {
    FoliaScheduler.runTask(
        TownsAndNations.getPlugin(),
        () -> {
          if (player.isOnline()) {
            try {
              updateAction.run();
              gui.update();
            } catch (Exception e) {
              logger.error("Error refreshing GUI for player {}", player.getName(), e);
            }
          }
        });
  }

  /**
   * Checks if a GUI should be reloaded based on cache timeout.
   *
   * @param lastLoadTime The timestamp of the last load (System.currentTimeMillis())
   * @param cacheDurationMillis Cache duration in milliseconds
   * @return true if cache has expired and should be reloaded
   */
  public static boolean shouldReload(long lastLoadTime, long cacheDurationMillis) {
    return (System.currentTimeMillis() - lastLoadTime) > cacheDurationMillis;
  }

  /**
   * Standard cache duration for GUI data (30 seconds).
   *
   * <p>Use this for data that doesn't change frequently but should be reasonably up-to-date.
   */
  public static final long CACHE_DURATION_STANDARD = 30_000L;

  /**
   * Short cache duration for GUI data (5 seconds).
   *
   * <p>Use this for data that changes frequently and needs to be current.
   */
  public static final long CACHE_DURATION_SHORT = 5_000L;

  /**
   * Long cache duration for GUI data (5 minutes).
   *
   * <p>Use this for static or rarely changing data like configuration.
   */
  public static final long CACHE_DURATION_LONG = 300_000L;
}
