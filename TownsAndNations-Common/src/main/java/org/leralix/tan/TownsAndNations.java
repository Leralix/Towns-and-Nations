package org.leralix.tan;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.leralix.lib.SphereLib;
import org.leralix.lib.data.PluginVersion;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.api.external.luckperms.LuckpermAPI;
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
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.DynamicLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.*;
import org.leralix.tan.listeners.chat.ChatListener;
import org.leralix.tan.listeners.interact.RightClickListener;
import org.leralix.tan.storage.ClaimBlacklistStorage;
import org.leralix.tan.storage.LocalChatStorage;
import org.leralix.tan.storage.database.DatabaseHandler;
import org.leralix.tan.storage.database.MySqlHandler;
import org.leralix.tan.storage.database.SQLiteHandler;
import org.leralix.tan.storage.impl.FortDataStorage;
import org.leralix.tan.storage.stored.*;
import org.leralix.tan.storage.stored.truce.TruceStorage;
import org.leralix.tan.tasks.DailyTasks;
import org.leralix.tan.tasks.SaveStats;
import org.leralix.tan.tasks.SecondTask;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.constants.DatabaseConstants;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.gameplay.TANCustomNBT;
import org.leralix.tan.utils.graphic.TeamUtils;
import org.leralix.tan.utils.text.NameFilter;
import org.leralix.tan.utils.text.NumberUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.TanAPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * Main Towns and Nations class, used to load the plugin and to manage the plugin.
 *
 * @author Leralix
 */
public class TownsAndNations extends JavaPlugin {

    public TownsAndNations() {
        super();
    }

    /**
     * Singleton instance of the plugin.
     */
    private static TownsAndNations plugin;
    /**
     * User agent used to access the GitHub API.
     */
    private static final String USER_AGENT = "Mozilla/5.0";
    /**
     * GitHub API link.
     */
    private static final String GITHUB_API_URL = "https://api.github.com/repos/leralix/towns-and-nations/releases/latest";
    /**
     * Current version of the plugin.
     * <p>
     * Used to check if the plugin is up-to-date to the latest version. Also
     * used to check if the plugin has just been updated and config file needs an update
     */
    private static final PluginVersion CURRENT_VERSION = new PluginVersion(0, 17, 1);

    private static final PluginVersion MINIMUM_SUPPORTING_DYNMAP = new PluginVersion(0, 16, 0);

    private static final PluginVersion MINIMUM_SUPPORTING_SPHERELIB = new PluginVersion(0, 6, 1);

    /**
     * The Latest version of the plugin on GitHub.
     * Used to check if the plugin is up to date to the latest version.
     */
    private PluginVersion latestVersion;

    /**
     * This variable is used to check when the plugin has launched
     * If the plugin close in less than 30 seconds, it is most likely a crash
     * during onEnable. Since a crash here might erase stored data, saving will not take place
     */
    private boolean loadedSuccessfully = false;
    /**
     * Database handler used to access the database.
     */
    private DatabaseHandler databaseHandler;

    /**
     * The storage of all playerData
     */
    private PlayerDataStorage playerDataStorage;

    /**
     * The storage of all towns
     */
    private TownDataStorage townDataStorage;

    private LocalChatStorage localChatStorage;

    private SaveStats saveStats;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        getLogger().log(Level.INFO, "\u001B[33m----------------Towns & Nations------------------\u001B[0m");
        getLogger().log(Level.INFO, "To report a bug or request a feature, please ask on my discord server: https://discord.gg/Q8gZSFUuzb");

        getLogger().log(Level.INFO, "[TaN] Loading Plugin");

        if (SphereLib.getPluginVersion().isOlderThan(MINIMUM_SUPPORTING_SPHERELIB)) {
            getLogger().log(Level.SEVERE, "[TaN] You need to update SphereLib to use this version of Towns and Nations");
            getLogger().log(Level.SEVERE, "[TaN] Please update SphereLib to version {0} or higher", MINIMUM_SUPPORTING_SPHERELIB);
            getLogger().log(Level.SEVERE, "[TaN] Disabling plugin");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }


        getLogger().log(Level.INFO, "[TaN] -Loading Lang");

        YamlConfiguration langConfig = ConfigUtil.saveAndUpdateResource(this, "lang.yml", Collections.emptyList());
        String lang = langConfig.getString("language", "en");

        File langFolder = new File(TownsAndNations.getPlugin().getDataFolder(), "lang");
        Lang.loadTranslations(langFolder, lang);
        DynamicLang.loadTranslations(langFolder, lang);
        getLogger().info(Lang.LANGUAGE_SUCCESSFULLY_LOADED.getDefault());

        getLogger().log(Level.INFO, "[TaN] -Loading Configs");

        List<String> mainBlackList = List.of(
                "claimBlacklist",
                "wildernessRules",
                "townPermissions",
                "regionPermissions",
                "propertyPermissions"
        );
        YamlConfiguration mainConfig = ConfigUtil.saveAndUpdateResource(this, "config.yml", mainBlackList);
        YamlConfiguration upgradesConfig =  ConfigUtil.saveAndUpdateResource(plugin, "upgrades.yml", List.of("upgrades", "region_upgrades", "nation_upgrades"));

        Constants.init(mainConfig, upgradesConfig);
        NameFilter.reload(mainConfig);
        ClaimBlacklistStorage.init(mainConfig);
        IconManager.getInstance();
        NumberUtil.init();
        FortStorage.init(new FortDataStorage());

        getLogger().log(Level.INFO, "[TaN] -Loading Economy");
        setupEconomy();

        getLogger().log(Level.INFO, "[TaN] -Loading Database");
        loadDB();

        getLogger().log(Level.INFO, "[TaN] -Loading Local data");

        playerDataStorage = new PlayerDataStorage();

        localChatStorage = new LocalChatStorage(playerDataStorage, mainConfig.getBoolean("sendPrivateMessagesToConsole", true));

        NationDataStorage.getInstance();
        RegionDataStorage.getInstance();
        NewClaimedChunkStorage.getInstance();
        townDataStorage = new TownDataStorage();
        townDataStorage.checkValidWorlds();
        if (Constants.enableNation()) {
            NationDataStorage.getInstance();
        }
        LandmarkStorage.getInstance();
        NewsletterStorage.getInstance();
        WarStorage.getInstance();
        EventManager.getInstance().registerEvents(new NewsletterEvents());
        TruceStorage.getInstance();
        FileUtil.setEnable(mainConfig.getBoolean("archiveHistory", false));
        TanChatUtils.init(playerDataStorage);
        TeamUtils.init(playerDataStorage);

        FortStorage.getInstance().checkValidWorlds();
        NewClaimedChunkStorage.getInstance().checkValidWorlds();

        this.saveStats = new SaveStats(this);

        getLogger().log(Level.INFO, "[TaN] -Loading blocks data");
        TANCustomNBT.setBlocsData(townDataStorage);


        getLogger().log(Level.INFO, "[TaN] -Registering Dependencies");

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().log(Level.INFO, "[TaN] -Registering PlaceholderAPI");
            new PlaceHolderAPI(
                    playerDataStorage,
                    townDataStorage,
                    RegionDataStorage.getInstance(),
                    NationDataStorage.getInstance(),
                    localChatStorage
                    ).register();
        }

        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            getLogger().log(Level.INFO, "[TaN] -Registering WorldGuard");
            WorldGuardManager.getInstance().register();
        }

        if(Bukkit.getPluginManager().isPluginEnabled("LuckPerms")){
            getLogger().log(Level.INFO, "[TaN] -Registering LuckPerms");
            var luckpermAPI = new LuckpermAPI();
            luckpermAPI.createContexts(
                    playerDataStorage,
                    townDataStorage,
                    RegionDataStorage.getInstance(),
                    NationDataStorage.getInstance(),
                    NewClaimedChunkStorage.getInstance()
            );
        }

        checkForUpdate();

        getLogger().log(Level.INFO, "[TaN] -Registering API");

        TanAPI.register(new InternalAPI(CURRENT_VERSION, this));

        initBStats();

        getLogger().log(Level.INFO, "[TaN] -Registering Tasks");
        saveStats.startSchedule();

        DailyTasks dailyTasks = new DailyTasks(playerDataStorage, Constants.getDailyTaskHour(), Constants.getDailyTaskMinute());
        dailyTasks.scheduleMidnightTask();
        SecondTask secondTask = new SecondTask(playerDataStorage);
        secondTask.startScheduler();

        getLogger().log(Level.INFO, "[TaN] -Loading commands");
        enableEventList();
        getCommand("tan").setExecutor(new PlayerCommandManager(playerDataStorage, townDataStorage, localChatStorage));
        getCommand("tanadmin").setExecutor(new AdminCommandManager(playerDataStorage));
        getCommand("tandebug").setExecutor(new DebugCommandManager(saveStats, dailyTasks));
        getCommand("tanserver").setExecutor(new ServerCommandManager(playerDataStorage, townDataStorage));

        loadedSuccessfully = true;
        getLogger().log(Level.INFO, "[TaN] Plugin loaded successfully");
        getLogger().info("\u001B[33m----------------Towns & Nations------------------\u001B[0m");
    }

    private void initBStats() {
        try {
            new Metrics(this, 20527);
        } catch (IllegalStateException e) {
            getLogger().log(Level.WARNING, "[TaN] Failed to submit stats to bStats : " + e.getMessage());
        }
    }

    private void loadDB() {

        DatabaseConstants constants = Constants.databaseConstants();

        String dbName = constants.getDbType();
        if (dbName != null && dbName.equalsIgnoreCase("sqlite")) {
            String dbPath = getDataFolder().getAbsolutePath() + "/database/main.db";
            databaseHandler = new SQLiteHandler(dbPath);
        }
        if (dbName != null && dbName.equalsIgnoreCase("mysql")) {
            databaseHandler = new MySqlHandler(
                    constants.getHost(),
                    constants.getPort(),
                    constants.getName(),
                    constants.getUser(),
                    constants.getPassword());
        }

        if (databaseHandler == null) {
            getLogger().log(Level.SEVERE, "[TaN] Invalid database type: " + dbName + ". Disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        try {
            databaseHandler.connect();
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "[TaN] Error while connecting to the database");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    /**
     * Method used to set up the economy of the server if Vault is enabled.
     */
    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().log(Level.INFO, "[TaN] -Vault is not detected. Running standalone economy");
            EconomyUtil.register(new TanEconomyStandalone());
            return;
        }
        VaultManager.setupVault();
    }

    /**
     * Disable the plugin
     * If the plugin has been closed less than 30 seconds after launch, the data will not be saved.
     */
    @Override
    public void onDisable() {
        if (!loadedSuccessfully) {
            getLogger().info("[TaN] Not saving data because plugin crashed during loading");
            getLogger().info("[TaN] Plugin disabled");
            return;
        }

        getLogger().info("[TaN] Savings Data");

        saveStats.saveAll();

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        getLogger().info("[TaN] Plugin disabled");
        getLogger().info("[00:23:37 INFO]: ----------------Towns & Nations------------------\n");
    }

    /**
     * Enable every event listener of the plugin.
     */
    private void enableEventList() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new ChatListener(playerDataStorage), this);
        pluginManager.registerEvents(new ChunkListener(playerDataStorage), this);
        pluginManager.registerEvents(new PlayerJoinListener(playerDataStorage), this);
        pluginManager.registerEvents(new PlayerEnterChunkListener(playerDataStorage), this);
        pluginManager.registerEvents(new ChatScopeListener(localChatStorage), this);
        pluginManager.registerEvents(new MobSpawnListener(), this);
        pluginManager.registerEvents(new SpawnListener(playerDataStorage), this);
        pluginManager.registerEvents(new PropertySignListener(playerDataStorage, townDataStorage), this);
        pluginManager.registerEvents(new LandmarkChestListener(playerDataStorage), this);
        pluginManager.registerEvents(new EconomyInitialiser(), this);
        pluginManager.registerEvents(new CommandBlocker(playerDataStorage), this);
        pluginManager.registerEvents(new AttackListener(playerDataStorage), this);
        pluginManager.registerEvents(new RightClickListener(playerDataStorage), this);
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
     * Check GitHub and notify admins if a new version of Towns and Nations is available.
     * This method is called when the plugin is enabled.
     */
    private void checkForUpdate() {
        // In case of any error, we consider the plugin is up to date
        latestVersion = CURRENT_VERSION;
        if (!TownsAndNations.getPlugin().getConfig().getBoolean("CheckForUpdate", true)) {
            getLogger().info("[TaN] Update check is disabled");
            return;
        }
        try {
            URL url = new URL(GITHUB_API_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setConnectTimeout(2000);
            con.setReadTimeout(2000);

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                }

                latestVersion = extractVersionFromResponse(response.toString());
                if (CURRENT_VERSION.isOlderThan(latestVersion)) {
                    getLogger().log(Level.INFO, "[TaN] A new version is available : {0}", latestVersion);
                } else {
                    getLogger().info("[TaN] Towns and Nation is up to date: " + CURRENT_VERSION);
                }
            } else {
                getLogger().info("[TaN] An error occurred while trying to accesses github API.");
                getLogger().info("[TaN] GitHub API response code: " + responseCode);
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "[TaN] An error occurred while trying to check for updates.", e);
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

    /**
     * Reset the singleton instance of the plugin.
     * Used for testing purposes only.
     * Remove it in a future version to replace singletons by dependency injection.
     */
    public void resetSingletonForTests() {
        RegionDataStorage.getInstance().reset();
        if (Constants.enableNation()) {
            NationDataStorage.getInstance().reset();
        }
        LandmarkStorage.getInstance().reset();
        WarStorage.getInstance().reset();
        NewClaimedChunkStorage.getInstance().reset();
    }

    public PlayerDataStorage getPlayerDataStorage() {
        return playerDataStorage;
    }

    public TownDataStorage getTownDataStorage() {
        return townDataStorage;
    }
}

