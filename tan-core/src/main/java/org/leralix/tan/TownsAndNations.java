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
import org.leralix.tan.service.EconomyService;
import org.leralix.tan.storage.ClaimBlacklistStorage;
import org.leralix.tan.storage.MobChunkSpawnStorage;
import org.leralix.tan.storage.database.DatabaseHandler;
import org.leralix.tan.storage.database.DatabaseHealthCheck;
import org.leralix.tan.storage.database.MySqlHandler;
import org.leralix.tan.storage.database.SQLiteHandler;
import org.leralix.tan.storage.impl.FortDataStorage;
import org.leralix.tan.storage.stored.*;
import org.leralix.tan.tasks.DailyTasks;
import org.leralix.tan.tasks.SaveStats;
import org.leralix.tan.tasks.SecondTask;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.constants.DatabaseConstants;
import org.leralix.tan.utils.constants.EnabledPermissions;
import org.leralix.tan.utils.gameplay.TANCustomNBT;
import org.leralix.tan.utils.text.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tan.api.TanAPI;

/**
 * Main Towns and Nations class, used to load the plugin and to manage the plugin.
 *
 * @author Leralix
 */
public class TownsAndNations extends JavaPlugin {

  private static final Logger LOGGER = LoggerFactory.getLogger(TownsAndNations.class);

  public TownsAndNations() {
    super();
  }

  /** Singleton instance of the plugin. */
  private static TownsAndNations plugin;

  /** User agent used to access the GitHub API. */
  private static final String USER_AGENT = "Mozilla/5.0";

  /** GitHub API link. */
  private static final String GITHUB_API_URL =
      "https://api.github.com/repos/leralix/towns-and-nations/releases/latest";

  /**
   * Current version of the plugin.
   *
   * <p>Used to check if the plugin is up-to-date to the latest version. Also used to check if the
   * plugin has just been updated and config file needs an update
   */
  private static final PluginVersion CURRENT_VERSION = new PluginVersion(0, 16, 0);

  private static final PluginVersion MINIMUM_SUPPORTING_DYNMAP = new PluginVersion(0, 14, 0);

  /**
   * The Latest version of the plugin on GitHub. Used to check if the plugin is up to date to the
   * latest version.
   */
  private PluginVersion latestVersion;

  /**
   * This variable is used to check when the plugin has launched If the plugin close in less than 30
   * seconds, it is most likely a crash during onEnable. Since a crash here might erase stored data,
   * saving will not take place
   */
  private boolean loadedSuccessfully = false;

  /** Database handler used to access the database. */
  private DatabaseHandler databaseHandler;

  /** Database health check for auto-reconnection */
  private DatabaseHealthCheck databaseHealthCheck;

  @Override
  public void onEnable() {
    // Plugin startup logic
    plugin = this;
    LOGGER.info("\u001B[33m----------------Towns & Nations------------------\u001B[0m");
    LOGGER.info(
        "To report a bug or request a feature, please ask on my discord server: https://discord.gg/Q8gZSFUuzb");

    LOGGER.info("[TaN] Loading Plugin");

    // if (SphereLib.getPluginVersion().isOlderThan(MINIMUM_SUPPORTING_SPHERELIB)) {
    //   LOGGER.error("[TaN] You need to update SphereLib to use this version of Towns and
    // Nations");
    //   LOGGER.error(
    //       "[TaN] Please update SphereLib to version "
    //           + MINIMUM_SUPPORTING_SPHERELIB
    //           + " or higher");
    //   LOGGER.error("[TaN] Disabling plugin");
    //   Bukkit.getPluginManager().disablePlugin(this);
    //   return;
    // }

    LOGGER.info("[TaN] -Loading Lang");

    ConfigUtil.saveAndUpdateResource(this, "lang.yml");
    ConfigUtil.addCustomConfig(this, "lang.yml", ConfigTag.LANG);
    String lang = ConfigUtil.getCustomConfig(ConfigTag.LANG).getString("language");

    File langFolder = new File(TownsAndNations.getPlugin().getDataFolder(), "lang");
    Lang.loadTranslations(langFolder, lang);
    DynamicLang.loadTranslations(langFolder, lang);
    LOGGER.info(Lang.LANGUAGE_SUCCESSFULLY_LOADED.getDefault());

    LOGGER.info("[TaN] -Loading Configs");

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

    LOGGER.info("[TaN] -Loading Configs");

    Constants.init(ConfigUtil.getCustomConfig(ConfigTag.MAIN));

    MobChunkSpawnStorage.init();
    ClaimBlacklistStorage.init();
    NewsletterType.init();
    IconManager.getInstance();
    NumberUtil.init();
    EnabledPermissions.getInstance().init();
    FortStorage.init(new FortDataStorage());

    LOGGER.info("[TaN] -Loading Economy");
    setupEconomy();

    LOGGER.info("[TaN] -Loading Database");
    loadDB();

    // P3.3: Initialize database health check for auto-reconnection
    if (databaseHandler != null) {
      databaseHealthCheck = new DatabaseHealthCheck(databaseHandler, this);
      databaseHealthCheck.start();
      LOGGER.info("[TaN] -Database health check started");
    }

    // OPTIMIZATION: Initialize Prometheus metrics collection
    try {
      PrometheusMetricsCollector metricsCollector = new PrometheusMetricsCollector();
      metricsCollector.startServer(9090);
      LOGGER.info("[TaN] -Prometheus metrics enabled on port 9090");
    } catch (Exception ex) {
      LOGGER.warn("[TaN] -Failed to initialize Prometheus metrics: " + ex.getMessage());
    }

    LOGGER.info("[TaN] -Loading Local data");

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

    LOGGER.info("[TaN] -Loading blocks data");
    TANCustomNBT.setBlocsData();

    LOGGER.info("[TaN] -Loading commands");
    SaveStats.startSchedule();

    DailyTasks dailyTasks =
        new DailyTasks(Constants.getDailyTaskHour(), Constants.getDailyTaskMinute());
    dailyTasks.scheduleMidnightTask();

    enableEventList();
    getCommand("tan").setExecutor(new PlayerCommandManager());
    getCommand("tanadmin").setExecutor(new AdminCommandManager());
    getCommand("tandebug").setExecutor(new DebugCommandManager());
    getCommand("tanserver").setExecutor(new ServerCommandManager());

    LOGGER.info("[TaN] -Registering Dependencies");

    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      LOGGER.info("[TaN] -Registering PlaceholderAPI");
      new PlaceHolderAPI().register();
    }

    if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
      LOGGER.info("[TaN] -Registering WorldGuard");
      WorldGuardManager.getInstance().register();
    }

    // checkForUpdate();

    LOGGER.info("[TaN] -Registering API");

    TanAPI.register(new InternalAPI(CURRENT_VERSION, MINIMUM_SUPPORTING_DYNMAP));

    initBStats();

    LOGGER.info("[TaN] -Registering Tasks");
    SecondTask secondTask = new SecondTask();
    secondTask.startScheduler();

    loadedSuccessfully = true;
    LOGGER.info("[TaN] Plugin loaded successfully");
    LOGGER.info("\u001B[33m----------------Towns & Nations------------------\u001B[0m");
  }

  private void initBStats() {
    try {
      new Metrics(this, 20527);
    } catch (IllegalStateException e) {
      LOGGER.warn("[TaN] Failed to submit stats to bStats");
    }
  }

  private void loadDB() {

    DatabaseConstants constants = Constants.databaseConstants();

    String dbName = constants.getDbType();
    if (dbName.equalsIgnoreCase("sqlite")) {
      String dbPath = getDataFolder().getAbsolutePath() + "/database/main.db";
      databaseHandler = new SQLiteHandler(dbPath);
    }
    if (dbName.equals("mysql")) {
      databaseHandler =
          new MySqlHandler(
              this,
              constants.getHost(),
              constants.getPort(),
              constants.getName(),
              constants.getUser(),
              constants.getPassword());
    }
    try {
      databaseHandler.connect();
    } catch (SQLException e) {
      LOGGER.error("[TaN] Error while connecting to the database");
    }
  }

  /** Method used to set up the economy of the server if Vault is enabled. */
  private void setupEconomy() {
    if (getServer().getPluginManager().getPlugin("Vault") == null) {
      LOGGER.info("[TaN] -Vault is not detected. Running standalone economy");
      EconomyUtil.register(new TanEconomyStandalone());
      return;
    }
    VaultManager.setupVault();
  }

  /**
   * Disable the plugin If the plugin has been closed less than 30 seconds after launch, the data
   * will not be saved.
   */
  @Override
  public void onDisable() {
    if (!loadedSuccessfully) {
      LOGGER.info("[TaN] Not saving data because plugin crashed during loading");
      LOGGER.info("[TaN] Plugin disabled");
      return;
    }

    LOGGER.info("[TaN] Savings Data");

    SaveStats.saveAll();

    // P3.3: Stop database health check before closing connection
    if (databaseHealthCheck != null) {
      databaseHealthCheck.stop();
      LOGGER.info("[TaN] -Database health check stopped");
    }

    // Wait for async save operations to complete (non-blocking approach)
    // Instead of Thread.sleep which blocks the main thread, we just log completion
    // The SaveStats.saveAll() should handle synchronization internally

    // P3.2: Close database connection pool properly
    if (databaseHandler != null) {
      try {
        databaseHandler.close();
      } catch (Exception e) {
        LOGGER.error("[TaN] Error closing database connection: " + e.getMessage());
      }
    }

    LOGGER.info("[TaN] Plugin disabled");
  }

  /** Enable every event listener of the plugin. */
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

  /**
   * Get the plugin instance
   *
   * @return the plugin instance
   */
  public static TownsAndNations getPlugin() {
    return plugin;
  }

  /**
   * Check GitHub and notify admins if a new version of Towns and Nations is available. This method
   * is called when the plugin is enabled.
   */
  @SuppressWarnings("unused") // Called conditionally from config
  private void checkForUpdate() {
    if (!TownsAndNations.getPlugin().getConfig().getBoolean("CheckForUpdate", true)) {
      LOGGER.info("[TaN] Update check is disabled");
      latestVersion = CURRENT_VERSION;
      return;
    }
    try {
      // P4.1: Use URI.toURL() instead of deprecated URL constructor
      URL url = java.net.URI.create(GITHUB_API_URL).toURL();
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("User-Agent", USER_AGENT);
      con.setConnectTimeout(5000); // 5 seconds connection timeout
      con.setReadTimeout(5000); // 5 seconds read timeout

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

  /**
   * Extract the version of the plugin from the response of the GitHub API.
   *
   * @param response the json of the GitHub API
   * @return the {@link PluginVersion Version} of the plugin.
   */
  private PluginVersion extractVersionFromResponse(String response) {
    JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
    String version = jsonResponse.get("tag_name").getAsString();
    return new PluginVersion(version);
  }

  /**
   * Check if the plugin is up-to-date to the latest version outside the main class
   *
   * @return true if the plugin is up-to-date, false otherwise.
   */
  public boolean isLatestVersion() {
    if (latestVersion == null) {
      return true; // Assume up-to-date if version check failed
    }
    return !CURRENT_VERSION.isOlderThan(latestVersion);
  }

  /**
   * Get the latest version of the plugin from GitHub
   *
   * @return the latest version of the plugin
   */
  public PluginVersion getLatestVersion() {
    return latestVersion;
  }

  /**
   * Get the current version of the plugin
   *
   * @return the current version of the plugin
   */
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

  /**
   * Reset the singleton instance of the plugin. Used for testing purposes only. Remove it in a
   * future version to replace singletons by dependency injection.
   */
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
