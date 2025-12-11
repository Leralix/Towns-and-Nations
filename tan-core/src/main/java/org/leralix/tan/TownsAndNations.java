package org.leralix.tan;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.leralix.lib.data.PluginVersion;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.api.external.papi.PlaceHolderAPI;
import org.leralix.tan.api.external.worldguard.WorldGuardManager;
import org.leralix.tan.api.internal.InternalAPI;
import org.leralix.tan.commands.admin.AdminCommandManager;
import org.leralix.tan.commands.debug.DebugCommandManager;
import org.leralix.tan.commands.player.PlayerCommandManager;
import org.leralix.tan.commands.server.ServerCommandManager;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.economy.TanEconomyStandalone;
import org.leralix.tan.economy.VaultManager;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.newsletter.NewsletterEvents;
import org.leralix.tan.events.newsletter.NewsletterStorage;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.DynamicLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.*;
import org.leralix.tan.listeners.chat.ChatListener;
import org.leralix.tan.listeners.interact.RightClickListener;
import org.leralix.tan.monitoring.PrometheusMetricsCollector;
import org.leralix.tan.redis.JedisManager;
import org.leralix.tan.redis.RedisClusterConfig;
import org.leralix.tan.redis.RedisServerConfig;
import org.leralix.tan.redis.RedisServerRegistry;
import org.leralix.tan.redis.RedisSyncManager;
import org.leralix.tan.service.EconomyService;
import org.leralix.tan.storage.ClaimBlacklistStorage;
import org.leralix.tan.storage.MobChunkSpawnStorage;
import org.leralix.tan.storage.database.DatabaseHandler;
import org.leralix.tan.storage.database.DatabaseHealthCheck;
import org.leralix.tan.storage.database.MySqlHandler;
import org.leralix.tan.storage.database.SQLiteHandler;
import org.leralix.tan.storage.impl.FortDataStorage;
import org.leralix.tan.storage.stored.*;
import org.leralix.tan.sync.TownSyncHandler;
import org.leralix.tan.sync.TownSyncService;
import org.leralix.tan.tasks.DailyTasks;
import org.leralix.tan.tasks.SaveStats;
import org.leralix.tan.tasks.SecondTask;
import org.leralix.tan.utils.CocoLogger;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.constants.DatabaseConstants;
import org.leralix.tan.utils.constants.EnabledPermissions;
import org.leralix.tan.utils.gameplay.TANCustomNBT;
import org.leralix.tan.utils.text.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tan.api.TanAPI;

public class TownsAndNations extends JavaPlugin {

  private static final Logger LOGGER = LoggerFactory.getLogger(TownsAndNations.class);

  public TownsAndNations() {
    super();
  }

  private static TownsAndNations plugin;

  private static final String USER_AGENT = "Mozilla/5.0";

  private static final String GITHUB_API_URL =
      "https://api.github.com/repos/leralix/towns-and-nations/releases/latest";

  private static final PluginVersion CURRENT_VERSION = new PluginVersion(0, 16, 0);

  private static final PluginVersion MINIMUM_SUPPORTING_DYNMAP = new PluginVersion(0, 14, 0);

  private PluginVersion latestVersion;

  private boolean loadedSuccessfully = false;

  private DatabaseHandler databaseHandler;

  private DatabaseHealthCheck databaseHealthCheck;

  private JedisManager redisClient;

  private RedisSyncManager redisSyncManager;

  private RedisServerConfig redisServerConfig;

  private RedisServerRegistry redisServerRegistry;

  private TownSyncService townSyncService;

  private TownSyncHandler townSyncHandler;

  @Override
  public void onEnable() {
    plugin = this;

    CocoLogger.printBanner();
    LOGGER.info(CocoLogger.info("Discord Support: https://discord.gg/Q8gZSFUuzb"));
    LOGGER.info("");

    CocoLogger.section("CHARGEMENT DE COCONATION");
    LOGGER.info(CocoLogger.loading("du plugin..."));

    LOGGER.info(CocoLogger.loading("des langues"));

    ConfigUtil.saveAndUpdateResource(this, "lang.yml");
    ConfigUtil.addCustomConfig(this, "lang.yml", ConfigTag.LANG);
    String lang = ConfigUtil.getCustomConfig(ConfigTag.LANG).getString("language");

    File langFolder = new File(TownsAndNations.getPlugin().getDataFolder(), "lang");
    Lang.loadTranslations(langFolder, lang);
    DynamicLang.loadTranslations(langFolder, lang);
    LOGGER.info(CocoLogger.success("Langue \"" + lang + "\" chargée avec succès"));

    LOGGER.info(CocoLogger.loading("des configurations"));

    List<String> mainBlackList = new ArrayList<>();
    mainBlackList.add("claimBlacklist");
    mainBlackList.add("wildernessRules");
    mainBlackList.add("allowedTimeSlotsWar");
    ConfigUtil.saveAndUpdateResource(this, "config.yml", mainBlackList);
    ConfigUtil.addCustomConfig(this, "config.yml", ConfigTag.MAIN);

    List<String> upgradeBlackList = new ArrayList<>();
    upgradeBlackList.add("upgrades");
    ConfigUtil.saveAndUpdateResource(this, "upgrades.yml", upgradeBlackList);
    ConfigUtil.addCustomConfig(this, "upgrades.yml", ConfigTag.UPGRADE);

    LOGGER.info(CocoLogger.success("Fichiers de configuration chargés"));

    Constants.init(ConfigUtil.getCustomConfig(ConfigTag.MAIN));

    MobChunkSpawnStorage.init();
    ClaimBlacklistStorage.init();
    NewsletterType.init();
    IconManager.getInstance();
    NumberUtil.init();
    EnabledPermissions.getInstance().init();
    FortStorage.init(new FortDataStorage());

    LOGGER.info(CocoLogger.loading("du système économique"));
    setupEconomy();

    LOGGER.info(CocoLogger.loading("de la base de données"));
    loadDB();

    if (databaseHandler != null && databaseHandler.getDataSource() != null) {
      LOGGER.info(CocoLogger.database("Initialisation des tables..."));
      org.leralix.tan.storage.TableInitializer tableInit =
          new org.leralix.tan.storage.TableInitializer(databaseHandler);
      tableInit.initializeAllTables();
      LOGGER.info(CocoLogger.success("Tables initialisées avec succès"));
    } else {
      LOGGER.error(
          CocoLogger.error(
              "Handler de base de données NULL - impossible d'initialiser les tables!"));
    }

    LOGGER.info(CocoLogger.loading("de Redis & du système de cache"));
    loadRedis();

    if (databaseHandler != null) {
      databaseHealthCheck = new DatabaseHealthCheck(databaseHandler, this);
      databaseHealthCheck.start();
      LOGGER.info(CocoLogger.success("Surveillance santé BDD activée (auto-reconnexion)"));
    }

    try {
      PrometheusMetricsCollector metricsCollector = new PrometheusMetricsCollector();
      metricsCollector.startServer(9090);
      LOGGER.info(CocoLogger.performance("Métriques Prometheus activées (port 9090)"));
    } catch (Exception ex) {
      LOGGER.warn(CocoLogger.warning("Échec initialisation Prometheus: " + ex.getMessage()));
    }

    LOGGER.info(CocoLogger.loading("des données locales"));

    RegionDataStorage.getInstance();
    PlayerDataStorage.getInstance();
    NewClaimedChunkStorage.getInstance();
    TownDataStorage.getInstance();
    LandmarkStorage.getInstance();
    PlannedAttackStorage.getInstance();
    NewsletterStorage.getInstance();
    WarStorage.getInstance();
    EventManager.getInstance().registerEvents(new NewsletterEvents());
    TruceStorage.getInstance();
    LOGGER.info(CocoLogger.success("Données locales chargées (9 storages)"));

    LOGGER.info(CocoLogger.loading("des blocs personnalisés"));
    TANCustomNBT.setBlocsData();

    LOGGER.info(CocoLogger.loading("des commandes"));
    SaveStats.startSchedule();

    DailyTasks dailyTasks =
        new DailyTasks(Constants.getDailyTaskHour(), Constants.getDailyTaskMinute());
    dailyTasks.scheduleMidnightTask();

    enableEventList();
    getCommand("coconation").setExecutor(new PlayerCommandManager());
    getCommand("coconationadmin").setExecutor(new AdminCommandManager());
    getCommand("coconationdebug").setExecutor(new DebugCommandManager());
    getCommand("coconationserver").setExecutor(new ServerCommandManager());
    LOGGER.info(CocoLogger.success("Commandes enregistrées (4 executeurs)"));

    LOGGER.info(CocoLogger.loading("des dépendances externes"));

    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      new PlaceHolderAPI().register();
      LOGGER.info(CocoLogger.success("PlaceholderAPI enregistré"));
    }

    if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
      WorldGuardManager.getInstance().register();
      LOGGER.info(CocoLogger.success("WorldGuard enregistré"));
    }

    TanAPI.register(new InternalAPI(CURRENT_VERSION, MINIMUM_SUPPORTING_DYNMAP));
    LOGGER.info(CocoLogger.success("API publique enregistrée (v" + CURRENT_VERSION + ")"));

    initBStats();

    SecondTask secondTask = new SecondTask();
    secondTask.startScheduler();
    LOGGER.info(CocoLogger.success("Tâches récurrentes démarrées"));

    try {
      org.leralix.tan.tasks.ReconciliationTask reconcile =
          new org.leralix.tan.tasks.ReconciliationTask();
      reconcile.start();
      LOGGER.info(CocoLogger.success("Réconciliation périodique démarrée"));
    } catch (Exception ex) {
      LOGGER.warn(CocoLogger.warning("Échec démarrage réconciliation: " + ex.getMessage()));
    }

    loadedSuccessfully = true;
    LOGGER.info(CocoLogger.boxed("COCONATION CHARGÉ AVEC SUCCÈS", CocoLogger.BRIGHT_GREEN));
  }

  private void initBStats() {
    try {
      new Metrics(this, 20527);
    } catch (IllegalStateException e) {
      LOGGER.warn(CocoLogger.warning("Échec envoi stats bStats"));
    }
  }

  private void loadDB() {

    DatabaseConstants constants = Constants.databaseConstants();

    String dbName = constants.getDbType();
    LOGGER.info(CocoLogger.database("Type de BDD: " + dbName));

    if (dbName.equalsIgnoreCase("sqlite")) {
      String dbPath = getDataFolder().getAbsolutePath() + "/database/main.db";
      LOGGER.info(CocoLogger.database("SQLite: " + dbPath));
      databaseHandler = new SQLiteHandler(dbPath);
    } else if (dbName.equals("mysql")) {
      String endpoint = constants.getHost() + ":" + constants.getPort() + "/" + constants.getName();
      LOGGER.info(CocoLogger.database("MySQL: " + endpoint));
      databaseHandler =
          new MySqlHandler(
              this,
              constants.getHost(),
              constants.getPort(),
              constants.getName(),
              constants.getUser(),
              constants.getPassword());
    } else {
      LOGGER.error(CocoLogger.error("❌ Type BDD inconnu: " + dbName + " (attendu: sqlite/mysql)"));
      LOGGER.error(CocoLogger.error("Plugin désactivé (config BDD invalide)"));
      Bukkit.getPluginManager().disablePlugin(this);
      return;
    }

    try {
      LOGGER.info("[TaN] Connecting to database...");
      databaseHandler.connect();
      LOGGER.info(CocoLogger.success("✓ Connexion BDD établie"));
    } catch (SQLException e) {
      LOGGER.error(CocoLogger.error("✖ ERREUR CRITIQUE: Échec connexion BDD!"));
      LOGGER.error(CocoLogger.error("Détails: " + e.getMessage()));
      e.printStackTrace();
      LOGGER.error(CocoLogger.error("Plugin désactivé (BDD inaccessible)"));
      Bukkit.getPluginManager().disablePlugin(this);
    }
  }

  private void loadRedis() {
    try {
      boolean redisEnabled =
          ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("redis.enabled", false);

      if (!redisEnabled) {
        LOGGER.info(CocoLogger.warning("Redis désactivé dans config"));
        return;
      }

      redisServerConfig = new RedisServerConfig(ConfigUtil.getCustomConfig(ConfigTag.MAIN));
      LOGGER.info(CocoLogger.info("🆔 Server ID: " + redisServerConfig.getServerId()));
      LOGGER.info(
          CocoLogger.info(
              "📡 Connexion Redis: "
                  + redisServerConfig.getHost()
                  + ":"
                  + redisServerConfig.getPort()
                  + " (DB: "
                  + redisServerConfig.getDatabase()
                  + ")"));

      redisClient =
          RedisClusterConfig.createRedisClient(ConfigUtil.getCustomConfig(ConfigTag.MAIN));

      if (redisClient != null && !redisClient.isClosed()) {
        LOGGER.info(CocoLogger.network("✓ Client Redis initialisé"));

        // Test de connexion
        try {
          boolean pingSuccess = redisClient.testConnection();
          if (pingSuccess) {
            LOGGER.info(CocoLogger.success("✓ Test de connexion Redis réussi"));
          } else {
            throw new Exception("PING failed");
          }
        } catch (Exception testEx) {
          LOGGER.error(CocoLogger.error("✖ Échec test connexion Redis: " + testEx.getMessage()));
          LOGGER.error(
              CocoLogger.error(
                  "Vérifiez: 1) Redis est démarré, 2) host/port corrects, 3) mot de passe valide"));
          throw testEx;
        }

        redisServerRegistry = new RedisServerRegistry(redisClient, redisServerConfig);
        redisServerRegistry.registerServer();

        redisServerRegistry.addServerEventListener(
            event -> {
              LOGGER.info(
                  CocoLogger.network(
                      String.format("🌐 Serveur %s: %s", event.getServerId(), event.getType())));
            });

        redisSyncManager = new RedisSyncManager(redisClient, redisServerConfig.getServerId());
        LOGGER.info(
            CocoLogger.network("⇄ Sync multi-serveur activé: " + redisServerConfig.getServerId()));

        townSyncService = new TownSyncService(redisSyncManager, redisServerConfig.getServerId());
        townSyncHandler = new TownSyncHandler(redisServerConfig.getServerId());

        LOGGER.info(CocoLogger.network("⇄ Module de synchronisation complet initialisé"));

        java.util.Set<String> activeServers = redisServerRegistry.getActiveServers();
        LOGGER.info(CocoLogger.success("🌐 Serveurs actifs: " + String.join(", ", activeServers)));
      } else {
        LOGGER.warn(CocoLogger.warning("⚠ Échec init client Redis - client null ou fermé"));
      }
    } catch (redis.clients.jedis.exceptions.JedisConnectionException e) {
      LOGGER.error(CocoLogger.error("✖ ERREUR: Impossible de se connecter à Redis"));
      LOGGER.error(CocoLogger.error("Cause: " + e.getMessage()));
      LOGGER.error(
          CocoLogger.error(
              "Solutions: Vérifiez que Redis est démarré et accessible sur "
                  + (redisServerConfig != null
                      ? redisServerConfig.getHost() + ":" + redisServerConfig.getPort()
                      : "l'hôte configuré")));
    } catch (redis.clients.jedis.exceptions.JedisDataException e) {
      LOGGER.error(CocoLogger.error("✖ ERREUR Redis: " + e.getMessage()));
      if (e.getMessage().contains("WRONGPASS") || e.getMessage().contains("NOAUTH")) {
        LOGGER.error(
            CocoLogger.error(
                "Erreur d'authentification - Vérifiez le mot de passe dans config.yml"));
        LOGGER.error(
            CocoLogger.error(
                "Utilisez password: null (ou commentez) si Redis n'a pas de mot de passe"));
      }
    } catch (Exception e) {
      LOGGER.error(CocoLogger.error("✖ Erreur inattendue lors de l'init Redis: " + e.getMessage()));
      LOGGER.error(CocoLogger.error("Type: " + e.getClass().getName()));
      e.printStackTrace();
    }
  }

  private void setupEconomy() {
    if (getServer().getPluginManager().getPlugin("Vault") == null) {
      LOGGER.info(CocoLogger.info("💰 Économie standalone (Vault non détecté)"));
      EconomyUtil.register(new TanEconomyStandalone());
      return;
    }
    VaultManager.setupVault();
  }

  @Override
  public void onDisable() {
    LOGGER.info(CocoLogger.boxed("ARRÊT DE COCONATION EN COURS", CocoLogger.BRIGHT_YELLOW));

    if (!loadedSuccessfully) {
      LOGGER.info(CocoLogger.error("✖ Sauvegarde annulée (crash au démarrage)"));
      LOGGER.info(CocoLogger.boxed("COCONATION DÉSACTIVÉ", CocoLogger.BRIGHT_RED));
      return;
    }

    LOGGER.info(CocoLogger.loading("sauvegarde des données"));

    SaveStats.saveAll();
    LOGGER.info(CocoLogger.success("Données sauvegardées"));

    if (databaseHealthCheck != null) {
      databaseHealthCheck.stop();
      LOGGER.info(CocoLogger.success("Surveillance santé BDD arrêtée"));
    }

    if (redisSyncManager != null) {
      try {
        redisSyncManager.shutdown();
        LOGGER.info(CocoLogger.success("Gestionnaire sync Redis arrêté"));
      } catch (Exception e) {
        LOGGER.error(CocoLogger.error("Échec arrêt sync Redis: " + e.getMessage()));
      }
    }

    if (redisServerRegistry != null) {
      try {
        redisServerRegistry.unregisterServer();
      } catch (Exception e) {
        LOGGER.error(CocoLogger.error("Échec désenregistrement serveur: " + e.getMessage()));
      }
    }

    if (redisClient != null && !redisClient.isClosed()) {
      try {
        redisClient.shutdown();
        LOGGER.info(CocoLogger.success("Client Redis arrêté"));
      } catch (Exception e) {
        LOGGER.error(CocoLogger.error("Échec arrêt Redis: " + e.getMessage()));
      }
    }

    if (databaseHandler != null) {
      try {
        databaseHandler.close();
        LOGGER.info(CocoLogger.success("Pool connexions BDD fermé"));
      } catch (Exception e) {
        LOGGER.error(CocoLogger.error("Échec fermeture BDD: " + e.getMessage()));
      }
    }

    LOGGER.info(CocoLogger.boxed("COCONATION DÉSACTIVÉ", CocoLogger.BRIGHT_YELLOW));
  }

  private void enableEventList() {
    PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(new ChatListener(), this);
    pluginManager.registerEvents(new ChunkListener(), this);
    pluginManager.registerEvents(new PlayerJoinListener(), this);
    pluginManager.registerEvents(new PlayerEnterChunkListener(), this);
    pluginManager.registerEvents(new ChatScopeListener(), this);
    pluginManager.registerEvents(new MobSpawnListener(), this);
    pluginManager.registerEvents(new SpawnListener(), this);
    pluginManager.registerEvents(new PropertySignListener(), this);
    pluginManager.registerEvents(new LandmarkChestListener(), this);
    pluginManager.registerEvents(new EconomyService(), this);
    pluginManager.registerEvents(new CommandBlocker(), this);

    pluginManager.registerEvents(new RightClickListener(), this);
  }

  public static TownsAndNations getPlugin() {
    return plugin;
  }

  @SuppressWarnings("unused")
  private void checkForUpdate() {
    if (!TownsAndNations.getPlugin().getConfig().getBoolean("CheckForUpdate", true)) {
      LOGGER.info("[TaN] Update check is disabled");
      latestVersion = CURRENT_VERSION;
      return;
    }
    try {
      URL url = java.net.URI.create(GITHUB_API_URL).toURL();
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("User-Agent", USER_AGENT);
      con.setConnectTimeout(5000);
      con.setReadTimeout(5000);

      int responseCode = con.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
          String inputLine;
          StringBuilder response = new StringBuilder();

          while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
          }

          latestVersion = extractVersionFromResponse(response.toString());
          if (CURRENT_VERSION.isOlderThan(latestVersion)) {
            LOGGER.info("[TaN] A new version is available : {0}", latestVersion);
          } else {
            LOGGER.info("[TaN] Towns and Nation is up to date: " + CURRENT_VERSION);
          }
        }
      } else {
        LOGGER.info("[TaN] An error occurred while trying to accesses github API.");
        LOGGER.info("[TaN] Error log : " + con.getInputStream());
      }
    } catch (Exception e) {
      LOGGER.warn("[TaN] An error occurred while trying to check for updates.");
      latestVersion = CURRENT_VERSION;
    }
  }

  private PluginVersion extractVersionFromResponse(String response) {
    JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
    String version = jsonResponse.get("tag_name").getAsString();
    return new PluginVersion(version);
  }

  public boolean isLatestVersion() {
    if (latestVersion == null) {
      return true;
    }
    return !CURRENT_VERSION.isOlderThan(latestVersion);
  }

  public PluginVersion getLatestVersion() {
    return latestVersion;
  }

  public PluginVersion getCurrentVersion() {
    return CURRENT_VERSION;
  }

  public PluginVersion getMinimumSupportingDynmap() {
    return MINIMUM_SUPPORTING_DYNMAP;
  }

  public DatabaseHandler getDatabaseHandler() {
    return databaseHandler;
  }

  public DatabaseHealthCheck getDatabaseHealthCheck() {
    return databaseHealthCheck;
  }

  public JedisManager getRedisClient() {
    return redisClient;
  }

  public RedisSyncManager getRedisSyncManager() {
    return redisSyncManager;
  }

  public RedisServerConfig getRedisServerConfig() {
    return redisServerConfig;
  }

  public RedisServerRegistry getRedisServerRegistry() {
    return redisServerRegistry;
  }

  public TownSyncService getTownSyncService() {
    return townSyncService;
  }

  public TownSyncHandler getTownSyncHandler() {
    return townSyncHandler;
  }

  public void resetSingletonForTests() {
    RegionDataStorage.getInstance().reset();
    PlayerDataStorage.getInstance().reset();
    TownDataStorage.getInstance().reset();
    LandmarkStorage.getInstance().reset();
    PlannedAttackStorage.getInstance().reset();
    WarStorage.getInstance().reset();
    NewClaimedChunkStorage.getInstance().reset();
  }
}
