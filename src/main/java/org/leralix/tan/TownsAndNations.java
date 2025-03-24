package org.leralix.tan;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.leralix.lib.data.PluginVersion;
import org.leralix.tan.api.InternalAPI;
import org.leralix.tan.commands.adminsubcommand.AdminCommandManager;
import org.leralix.tan.commands.debugsubcommand.DebugCommandManager;
import org.leralix.tan.commands.playersubcommand.PlayerCommandManager;
import org.leralix.tan.commands.server.ServerCommandManager;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.economy.TanEconomyStandalone;
import org.leralix.tan.economy.VaultManager;
import org.leralix.tan.listeners.*;
import org.leralix.tan.listeners.chat.ChatListener;
import org.leralix.tan.newsletter.NewsletterStorage;
import org.leralix.tan.storage.ClaimBlacklistStorage;
import org.leralix.tan.storage.MobChunkSpawnStorage;
import org.leralix.tan.storage.database.DatabaseHandler;
import org.leralix.tan.storage.database.SQLiteHandler;
import org.leralix.tan.storage.legacy.UpgradeStorage;
import org.leralix.tan.storage.stored.*;
import org.leralix.tan.tasks.DailyTasks;
import org.leralix.tan.tasks.SaveStats;
import org.leralix.tan.utils.CustomNBT;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.api.PlaceHolderAPI;
import org.leralix.tan.lang.DynamicLang;
import org.leralix.tan.lang.Lang;
import org.tan.api.TanAPI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Main Towns and Nations class, used to load the plugin and to manage the plugin.
 * @author Leralix
 */
public final class TownsAndNations extends JavaPlugin {

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
    private static final PluginVersion CURRENT_VERSION = new PluginVersion(0,14,0);
    private static final PluginVersion MINIMUM_SUPPORTING_DYNMAP = new PluginVersion(0,11,0);

    /**
     * Latest version of the plugin from GitHub.
     * Used to check if the plugin is up-to-date to the latest version.
     */
    private PluginVersion latestVersion;

    /**
     * If enabled, current player usernames will be from the color of the relation.
     * This option cannot be enabled with allowTownTag.
     */
    private boolean allowColorCodes = false;
    /**
     * If Enabled, player username will have a 3 letter prefix of their town name.
     * This option cannot be enabled with allowColorCodes.
     */
    private boolean allowTownTag = false;
    /**
     * This variable is used to check when the plugin has launched
     * If the plugin close in less than 30 seconds, it is most likely a crash
     * during onEnable. Since a crash here might erase stored data, saving will not take place
     */
    private final long dateOfStart = System.currentTimeMillis();
    /**
     * Database handler used to access the database.
     */
    private DatabaseHandler databaseHandler;

    PlayerDataStorage playerDataStorage;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        getLogger().log(Level.INFO, "\u001B[33m----------------Towns & Nations------------------\u001B[0m");
        getLogger().log(Level.INFO,"To report a bug or request a feature, please ask on my discord server: https://discord.gg/Q8gZSFUuzb");

        getLogger().log(Level.INFO,"[TaN] Loading Plugin");


        getLogger().log(Level.INFO,"[TaN] -Loading Lang");

        ConfigUtil.saveAndUpdateResource(this, "lang.yml");
        ConfigUtil.addCustomConfig(this, "lang.yml", ConfigTag.LANG);
        String lang = ConfigUtil.getCustomConfig(ConfigTag.LANG).getString("language");


        Lang.loadTranslations(lang);
        DynamicLang.loadTranslations(lang);
        getLogger().info(Lang.LANGUAGE_SUCCESSFULLY_LOADED.get());

        getLogger().log(Level.INFO, "[TaN] -Loading Configs");
        List<String> blackList = new ArrayList<>();
        blackList.add("claimBlacklist");

        ConfigUtil.saveAndUpdateResource(this, "config.yml", blackList);
        ConfigUtil.addCustomConfig(this, "config.yml", ConfigTag.MAIN);
        ConfigUtil.saveAndUpdateResource(this, "townUpgrades.yml");
        ConfigUtil.addCustomConfig(this, "townUpgrades.yml", ConfigTag.UPGRADE);


        getLogger().log(Level.INFO, "[TaN] -Loading Database");
        loadDB();

        getLogger().log(Level.INFO, "[TaN] -Loading Economy");
        setupEconomy();

        getLogger().log(Level.INFO, "[TaN] -Loading Storage");

        UpgradeStorage.init();
        MobChunkSpawnStorage.init();
        ClaimBlacklistStorage.init();

        FileConfiguration mainConfig = ConfigUtil.getCustomConfig(ConfigTag.MAIN);
        allowColorCodes = mainConfig.getBoolean("EnablePlayerColorCode", false);
        allowTownTag = mainConfig.getBoolean("EnablePlayerPrefix",false);



        getLogger().log(Level.INFO,"[TaN] -Loading Local data");

        RegionDataStorage.getInstance().loadStats();
        this.playerDataStorage = PlayerDataStorage.getInstance();
        NewClaimedChunkStorage.getInstance();
        TownDataStorage.getInstance();
        LandmarkStorage.getInstance().load();
        PlannedAttackStorage.load();
        NewsletterStorage.load();


        getLogger().log(Level.INFO,"[TaN] -Loading blocks data");
        CustomNBT.setBlocsData();


        getLogger().log(Level.INFO,"[TaN] -Loading commands");
        SaveStats.startSchedule();
        DailyTasks.scheduleMidnightTask();

        enableEventList();
        getCommand("tan").setExecutor(new PlayerCommandManager());
        getCommand("tanadmin").setExecutor(new AdminCommandManager());
        getCommand("tandebug").setExecutor(new DebugCommandManager());
        getCommand("tanserver").setExecutor(new ServerCommandManager());


        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().log(Level.INFO,"[TaN] -Loading PlaceholderAPI");
            new PlaceHolderAPI().register();
        }

        checkForUpdate();

        getLogger().log(Level.INFO,"[TaN] -Registering API");

        TanAPI.register(new InternalAPI(CURRENT_VERSION,MINIMUM_SUPPORTING_DYNMAP));
        new Metrics(this, 20527);

        getLogger().log(Level.INFO,"[TaN] Plugin loaded successfully");
        getLogger().info("\u001B[33m----------------Towns & Nations------------------\u001B[0m");
    }

    private void loadDB() {
        String dbName = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getString("dbType", "sqlite");
        if(dbName.equalsIgnoreCase("sqlite"))
            databaseHandler = new SQLiteHandler(getDataFolder().getAbsolutePath() + "/database/main.db");
        try {
            databaseHandler.connect();
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE,"[TaN] Error while connecting to the database");
        }
    }

    /**
     * Method used to set up the economy of the server if Vault is enabled.
     */
    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().log(Level.INFO,"[TaN] -Vault is not detected. Running standalone economy");
            EconomyUtil.setEconomy(new TanEconomyStandalone());
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
        if(System.currentTimeMillis() - dateOfStart < 30000){
            getLogger().info("[TaN] Not saving data because plugin was closed less than 30s after launch");
            getLogger().info("[TaN] Plugin disabled");
            return;
        }

        getLogger().info("[TaN] Savings Data");


        RegionDataStorage.getInstance().saveStats();
        TownDataStorage.getInstance().saveStats();
        playerDataStorage.saveStats();
        NewClaimedChunkStorage.getInstance().save();
        LandmarkStorage.getInstance().save();
        PlannedAttackStorage.save();
        NewsletterStorage.save();

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        getLogger().info("[TaN] Plugin disabled");
    }

    /**
     * Enable every event listener of the plugin.
     */
    private void enableEventList() {
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new ChunkListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerEnterChunkListener(), this);
        getServer().getPluginManager().registerEvents(new ChatScopeListener(), this);
        getServer().getPluginManager().registerEvents(new MobSpawnListener(), this);
        getServer().getPluginManager().registerEvents(new SpawnListener(), this);
        getServer().getPluginManager().registerEvents(new CreatePropertyListener(),this);
        getServer().getPluginManager().registerEvents(new PropertySignListener(),this);
        getServer().getPluginManager().registerEvents(new LandmarkChestListener(),this);
        getServer().getPluginManager().registerEvents(new AttackListener(),this);
    }

    /**
     * Get the plugin instance
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
        if(!TownsAndNations.getPlugin().getConfig().getBoolean("CheckForUpdate",true)){
            getLogger().info("[TaN] Update check is disabled");
            latestVersion = CURRENT_VERSION;
            return;
        }
        try {
            URL url = new URL(GITHUB_API_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                latestVersion = extractVersionFromResponse(response.toString());
                if (CURRENT_VERSION.isOlderThan(latestVersion)) {
                    getLogger().log(Level.INFO,"[TaN] A new version is available : {0}", latestVersion);
                } else {
                    getLogger().info("[TaN] Towns and Nation is up to date: "+ CURRENT_VERSION);
                }
            } else {
                getLogger().info("[TaN] An error occurred while trying to accesses github API.");
                getLogger().info("[TaN] Error log : " + con.getInputStream());
            }
        } catch (Exception e) {
            getLogger().warning("[TaN] An error occurred while trying to check for updates.");
        }
    }

    /**
     * Extract the version of the plugin from the response of the GitHub API.
     * @param response the json of the GitHub API
     * @return the {@link PluginVersion Version} of the plugin.
     */
    private PluginVersion extractVersionFromResponse(String response) {
        JsonParser parser = new JsonParser();
        JsonObject jsonResponse = parser.parse(response).getAsJsonObject();
        String version = jsonResponse.get("tag_name").getAsString();
        return new PluginVersion(version);
    }

    /**
     * Check if the plugin is up-to-date to the latest version outside the main class
     * @return true if the plugin is up-to-date, false otherwise.
     */
    public boolean isLatestVersion(){
        return !CURRENT_VERSION.isOlderThan(latestVersion);
    }

    /**
     * Get the latest version of the plugin from GitHub
     * @return the latest version of the plugin
     */
    public PluginVersion getLatestVersion(){
        return latestVersion;
    }

    /**
     * Get the current version of the plugin
     * @return the current version of the plugin
     */
    public PluginVersion getCurrentVersion() {
        return CURRENT_VERSION;
    }


    /**
     * Check if the color code is enabled
     * @return true if color code is enabled, false otherwise.
     */
    public boolean colorCodeIsNotEnabled(){
        return !allowColorCodes;
    }

    /**
     * Check if the town tag is enabled
     * @return true if town tag is enabled, false otherwise.
     */
    public boolean townTagIsEnabled(){
        return allowTownTag;
    }

    public PluginVersion getMinimumSupportingDynmap() {
        return MINIMUM_SUPPORTING_DYNMAP;
    }

    public DatabaseHandler getDatabaseHandler() {
        return databaseHandler;
    }
}