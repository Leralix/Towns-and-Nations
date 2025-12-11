package org.leralix.tan.storage.stored;

import com.google.gson.GsonBuilder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.NoPlayerData;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.storage.exceptions.DatabaseNotReadyException;
import org.leralix.tan.utils.FoliaScheduler;

public class PlayerDataStorage extends DatabaseStorage<ITanPlayer> {

  private static final String ERROR_MESSAGE = "Error while creating player storage";
  private static final String TABLE_NAME = "tan_players";

  private static final int MAX_RETRY_ATTEMPTS = 3;
  private static final long RETRY_DELAY_MS = 500;

  private static volatile PlayerDataStorage instance;

  private static ITanPlayer NO_PLAYER;

  private PlayerDataStorage() {
    super(
        TABLE_NAME,
        ITanPlayer.class,
        new GsonBuilder()
            .registerTypeAdapter(ITanPlayer.class, new ITanPlayerAdapter())
            .setPrettyPrinting()
            .create());
  }

  public static PlayerDataStorage getInstance() {
    if (instance == null) {
      synchronized (PlayerDataStorage.class) {
        if (instance == null) {
          instance = new PlayerDataStorage();
          NO_PLAYER = new NoPlayerData();
        }
      }
    }
    return instance;
  }

  @Override
  protected void createTable() {
    String createTableSQL =
        """
            CREATE TABLE IF NOT EXISTS %s (
                id VARCHAR(255) PRIMARY KEY,
                data TEXT NOT NULL
            )
        """
            .formatted(TABLE_NAME);

    try (Connection conn = getDatabase().getDataSource().getConnection();
        Statement stmt = conn.createStatement()) {
      TownsAndNations.getPlugin().getLogger().info("[TaN-DB] Creating table: " + TABLE_NAME);
      stmt.execute(createTableSQL);
      TownsAndNations.getPlugin()
          .getLogger()
          .info("[TaN-DB] Table " + TABLE_NAME + " created/verified successfully");

      try (ResultSet rs = conn.getMetaData().getColumns(null, null, TABLE_NAME, "player_name")) {
        if (!rs.next()) {
          stmt.executeUpdate(
              "ALTER TABLE %s ADD COLUMN player_name VARCHAR(255)".formatted(TABLE_NAME));
          TownsAndNations.getPlugin().getLogger().info("Added player_name column to " + TABLE_NAME);
        }
      }

      try (ResultSet rs = conn.getMetaData().getColumns(null, null, TABLE_NAME, "town_name")) {
        if (!rs.next()) {
          stmt.executeUpdate(
              "ALTER TABLE %s ADD COLUMN town_name VARCHAR(255)".formatted(TABLE_NAME));
          TownsAndNations.getPlugin().getLogger().info("Added town_name column to " + TABLE_NAME);
        }
      }

      try (ResultSet rs = conn.getMetaData().getColumns(null, null, TABLE_NAME, "nation_name")) {
        if (!rs.next()) {
          stmt.executeUpdate(
              "ALTER TABLE %s ADD COLUMN nation_name VARCHAR(255)".formatted(TABLE_NAME));
          TownsAndNations.getPlugin().getLogger().info("Added nation_name column to " + TABLE_NAME);
        }
      }

      try (ResultSet rs = conn.getMetaData().getColumns(null, null, TABLE_NAME, "last_seen")) {
        if (!rs.next()) {
          if (getDatabase().isMySQL()) {
            stmt.executeUpdate(
                "ALTER TABLE %s ADD COLUMN last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
                    .formatted(TABLE_NAME));
          } else {
            stmt.executeUpdate(
                "ALTER TABLE %s ADD COLUMN last_seen DATETIME DEFAULT CURRENT_TIMESTAMP"
                    .formatted(TABLE_NAME));
          }
          TownsAndNations.getPlugin().getLogger().info("Added last_seen column to " + TABLE_NAME);
        }
      }

    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe("Error creating table " + TABLE_NAME + ": " + e.getMessage());
    }
  }

  @Override
  protected void createIndexes() {
    String createPlayerNameIndexSQL =
        "CREATE INDEX IF NOT EXISTS idx_player_name ON " + TABLE_NAME + " (player_name)";
    String createTownNameIndexSQL =
        "CREATE INDEX IF NOT EXISTS idx_player_town ON " + TABLE_NAME + " (town_name)";
    String createNationNameIndexSQL =
        "CREATE INDEX IF NOT EXISTS idx_player_nation ON " + TABLE_NAME + " (nation_name)";

    try (Connection conn = getDatabase().getDataSource().getConnection();
        Statement stmt = conn.createStatement()) {
      stmt.execute(createPlayerNameIndexSQL);
      stmt.execute(createTownNameIndexSQL);
      stmt.execute(createNationNameIndexSQL);
      TownsAndNations.getPlugin().getLogger().info("Created indexes on " + TABLE_NAME);
    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .warning("Error creating indexes for " + TABLE_NAME + ": " + e.getMessage());
    }
  }

  @Override
  public void put(String id, ITanPlayer obj) {
    if (id == null || obj == null) {
      return;
    }

    String jsonData = gson.toJson(obj, typeToken);
    String upsertSQL;
    if (getDatabase().isMySQL()) {
      upsertSQL =
          "INSERT INTO "
              + tableName
              + " (id, player_name, town_name, nation_name, data) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE player_name = VALUES(player_name), town_name = VALUES(town_name), nation_name = VALUES(nation_name), data = VALUES(data)";
    } else {
      upsertSQL =
          "INSERT OR REPLACE INTO "
              + tableName
              + " (id, player_name, town_name, nation_name, data) VALUES (?, ?, ?, ?, ?)";
    }

    FoliaScheduler.runTaskAsynchronously(
        TownsAndNations.getPlugin(),
        () -> {
          try (Connection conn = getDatabase().getDataSource().getConnection();
              PreparedStatement ps = conn.prepareStatement(upsertSQL)) {

            ps.setString(1, id);
            ps.setString(2, obj.getNameStored());
            ps.setString(3, obj.getTownName());
            ps.setString(4, obj.getNationName());
            ps.setString(5, jsonData);
            ps.executeUpdate();

            if (cacheEnabled && cache != null) {
              synchronized (cache) {
                cache.put(id, obj);
              }
            }

          } catch (SQLException e) {
            TownsAndNations.getPlugin()
                .getLogger()
                .severe(
                    "Error storing "
                        + typeClass.getSimpleName()
                        + " with ID "
                        + id
                        + ": "
                        + e.getMessage());
          }
        });
  }

  public CompletableFuture<ITanPlayer> register(Player p) {
    ITanPlayer tanPlayer = new PlayerData(p);
    return register(tanPlayer);
  }

  CompletableFuture<ITanPlayer> register(ITanPlayer p) {
    CompletableFuture<ITanPlayer> future = new CompletableFuture<>();
    put(p.getID(), p);
    future.complete(p);
    return future;
  }

  public CompletableFuture<ITanPlayer> get(OfflinePlayer player) {
    return get(player.getUniqueId().toString());
  }

  public CompletableFuture<ITanPlayer> get(Player player) {
    return get(player.getUniqueId().toString());
  }

  public CompletableFuture<ITanPlayer> get(UUID playerID) {
    return get(playerID.toString());
  }

  @Override
  public CompletableFuture<ITanPlayer> get(String id) {
    CompletableFuture<ITanPlayer> future = new CompletableFuture<>();
    if (id == null) {
      future.complete(NO_PLAYER);
      return future;
    }

    getWithRetry(id, 1, future);
    return future;
  }

  private void getWithRetry(String id, int attemptNumber, CompletableFuture<ITanPlayer> future) {
    super.get(id)
        .thenAccept(
            res -> {
              if (res != null) {
                future.complete(res);
              } else {
                createNewPlayerProfile(id, future);
              }
            })
        .exceptionally(
            ex -> {
              if (ex.getCause() instanceof DatabaseNotReadyException) {
                if (attemptNumber < MAX_RETRY_ATTEMPTS) {
                  TownsAndNations.getPlugin()
                      .getLogger()
                      .warning(
                          String.format(
                              "[TaN] Database not ready for player %s (attempt %d/%d). Retrying in %dms...",
                              id, attemptNumber, MAX_RETRY_ATTEMPTS, RETRY_DELAY_MS));

                  FoliaScheduler.runTaskLaterAsynchronously(
                      TownsAndNations.getPlugin(),
                      () -> getWithRetry(id, attemptNumber + 1, future),
                      RETRY_DELAY_MS / 50);
                } else {
                  TownsAndNations.getPlugin()
                      .getLogger()
                      .severe(
                          String.format(
                              "[TaN] CRITICAL: Failed to load player %s after %d attempts. Database may be down!",
                              id, MAX_RETRY_ATTEMPTS));
                  future.completeExceptionally(
                      new RuntimeException(
                          "Failed to load player after " + MAX_RETRY_ATTEMPTS + " attempts", ex));
                }
              } else {
                TownsAndNations.getPlugin()
                    .getLogger()
                    .severe(
                        String.format(
                            "[TaN] Non-recoverable error loading player %s: %s",
                            id, ex.getMessage()));
                future.completeExceptionally(ex);
              }
              return null;
            });
  }

  private void createNewPlayerProfile(String id, CompletableFuture<ITanPlayer> future) {
    FoliaScheduler.runTask(
        TownsAndNations.getPlugin(),
        () -> {
          Player newPlayer = Bukkit.getPlayer(UUID.fromString(id));
          if (newPlayer != null) {
            TownsAndNations.getPlugin()
                .getLogger()
                .info(
                    String.format(
                        "[TaN] Creating new player profile for %s (%s)", newPlayer.getName(), id));

            ITanPlayer newTanPlayer = new PlayerData(newPlayer);
            register(newTanPlayer)
                .thenAccept(
                    registeredPlayer -> {
                      future.complete(registeredPlayer);
                    })
                .exceptionally(
                    ex -> {
                      future.completeExceptionally(ex);
                      return null;
                    });
          } else {
            future.completeExceptionally(
                new RuntimeException("Error: Player ID [" + id + "] has not been found"));
          }
        });
  }

  @Override
  public void reset() {
    instance = null;
  }

  public ITanPlayer getSync(String id) {
    if (cacheEnabled && cache != null) {
      ITanPlayer cached = (ITanPlayer) cache.get(id);
      if (cached != null) {
        return cached;
      }
    }

    get(id)
        .thenAccept(
            player -> {
              if (player != null && cacheEnabled && cache != null) {
                cache.put(id, player);
              }
            });

    return NO_PLAYER;
  }

  public ITanPlayer getSync(UUID playerID) {
    return getSync(playerID.toString());
  }

  public ITanPlayer getSync(Player player) {
    return getSync(player.getUniqueId());
  }

  public ITanPlayer getSync(OfflinePlayer player) {
    return getSync(player.getUniqueId());
  }
}
