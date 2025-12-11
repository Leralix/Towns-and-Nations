package org.leralix.tan.utils.gui;

import dev.triumphteam.gui.guis.Gui;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.gui.circuitbreaker.GuiCircuitBreaker;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.FoliaScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncGuiHelper {

  private static final Logger logger = LoggerFactory.getLogger(AsyncGuiHelper.class);

  private static final GuiCircuitBreaker guiCircuitBreaker = new GuiCircuitBreaker(5, 60000);

  private AsyncGuiHelper() {
    throw new IllegalStateException("Utility class");
  }

  public static <T> void loadAsync(
      Player player, Supplier<T> asyncLoader, Consumer<T> mainThreadConsumer) {
    FoliaScheduler.runTaskAsynchronously(
        TownsAndNations.getPlugin(),
        () -> {
          try {
            T data = asyncLoader.get();

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

  public static void prefetchPlayerData(Player player, Consumer<ITanPlayer> guiCreator) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              if (player.isOnline() && tanPlayer != null) {
                FoliaScheduler.runTask(
                    TownsAndNations.getPlugin(),
                    () -> {
                      try {
                        guiCreator.accept(tanPlayer);
                      } catch (Exception e) {
                        logger.error(
                            "Error creating GUI for player {} with prefetched data",
                            player.getName(),
                            e);
                      }
                    });
              }
            })
        .exceptionally(
            ex -> {
              logger.error("Error prefetching player data for {}", player.getName(), ex);
              return null;
            });
  }

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

  public static boolean shouldReload(long lastLoadTime, long cacheDurationMillis) {
    return (System.currentTimeMillis() - lastLoadTime) > cacheDurationMillis;
  }

  public static final long CACHE_DURATION_STANDARD = 30_000L;

  public static final long CACHE_DURATION_SHORT = 5_000L;

  public static final long CACHE_DURATION_LONG = 300_000L;

  public static void executeWithCircuitBreaker(
      Player player, Runnable guiOperation, Runnable onSuccess, Consumer<Throwable> onFailure) {

    guiCircuitBreaker.execute(
        guiOperation,
        ex -> {
          logger.debug("GUI operation succeeded for player {}", player.getName());
          onSuccess.run();
        },
        ex -> {
          logger.warn("GUI operation failed for player {}: {}", player.getName(), ex.getMessage());
          onFailure.accept(ex);
        });
  }

  public static String getCircuitBreakerState() {
    return guiCircuitBreaker.getState();
  }
}
