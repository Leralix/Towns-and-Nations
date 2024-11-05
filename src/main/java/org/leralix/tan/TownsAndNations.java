package org.leralix.tan;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.leralix.tan.dataclass.PluginVersion;
import org.leralix.tan.economy.*;
import org.leralix.tan.lang.DynamicLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.api.PlaceHolderAPI;
import org.leralix.tan.newsletter.NewsletterStorage;
import org.leralix.tan.storage.ClaimBlacklistStorage;
import org.leralix.tan.tasks.DailyTasks;
import org.leralix.tan.tasks.SaveStats;
import org.leralix.tan.api.TanApi;
import org.leralix.tan.commands.adminsubcommand.AdminCommandManager;
import org.leralix.tan.commands.debugsubcommand.DebugCommandManager;
import org.leralix.tan.commands.playersubcommand.PlayerCommandManager;
import org.leralix.tan.listeners.*;
import org.leralix.tan.listeners.ChatListener.ChatListener;
import org.leralix.tan.storage.CustomItemManager;
import org.leralix.tan.storage.stored.*;
import org.leralix.tan.storage.legacy.UpgradeStorage;
import org.leralix.tan.storage.MobChunkSpawnStorage;
import org.leralix.tan.storage.SoundStorage;
import org.leralix.tan.utils.CustomNBT;
import org.leralix.tan.utils.DropChances;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main Towns and Nations class, used to load the plugin and to manage the plugin.
 * @author Leralix
 */
public final class TownsAndNations extends JavaPlugin {


    private static TownsAndNations plugin;
    Logger logger;
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
    private static final PluginVersion CURRENT_VERSION = new PluginVersion(0,11,4);
    private static final PluginVersion MINIMUM_SUPPORTING_DYNMAP = new PluginVersion(0,6,0);

    /**
     * Latest version of the plugin from GitHub.
     * Used to check if the plugin is up-to-date to the latest version.
     */
    private PluginVersion latestVersion;
    /**
     * Class used to access the API of the plugin.
     */
    private TanApi api;
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
     * If enabled, the Towns and Nations dynmap plugin is installed.
     * This will enable new features for the core plugin.
     */
    private boolean dynmapAddonLoaded = true;
    /**
     * This variable is used to check when the plugin has launched
     * If the plugin close in less than 30 seconds, it is most likely a crash
     * during onEnable. Since a crash here might erase stored data, saving will not take place
     */
    private final long dateOfStart = System.currentTimeMillis();
    /**
     * This method is called when the plugin is enabled.
     * It is used to load the plugin and all its features.
     */

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        logger = this.getLogger();
        getLogger().log(Level.INFO, "\u001B[33m----------------Towns & Nations------------------\u001B[0m");
        getLogger().log(Level.INFO,"To report a bug or request a feature, please ask on my discord server: https://discord.gg/Q8gZSFUuzb");

        logger.log(Level.INFO,"[TaN] Loading Plugin");

        checkForUpdate();

        logger.log(Level.INFO,"[TaN] -Loading Lang");

        ConfigUtil.saveAndUpdateResource("lang.yml");
        ConfigUtil.addCustomConfig("lang.yml", ConfigTag.LANG);
        String lang = ConfigUtil.getCustomConfig(ConfigTag.LANG).getString("language");


        Lang.loadTranslations(lang);
        DynamicLang.loadTranslations(lang);
        TownsAndNations.getPlugin().getPluginLogger().info(Lang.LANGUAGE_SUCCESSFULLY_LOADED.get());


        logger.log(Level.INFO, "[TaN] -Loading Configs");
        ConfigUtil.saveAndUpdateResource("config.yml");
        ConfigUtil.addCustomConfig("config.yml", ConfigTag.MAIN);
        ConfigUtil.saveAndUpdateResource("townUpgrades.yml");
        ConfigUtil.addCustomConfig("townUpgrades.yml", ConfigTag.UPGRADES);

        DropChances.load();
        UpgradeStorage.init();
        MobChunkSpawnStorage.init();
        SoundStorage.init();
        CustomItemManager.loadCustomItems();
        ClaimBlacklistStorage.init();

        FileConfiguration mainConfig = ConfigUtil.getCustomConfig(ConfigTag.MAIN);
        allowColorCodes = mainConfig.getBoolean("EnablePlayerColorCode", false);
        allowTownTag = mainConfig.getBoolean("EnablePlayerPrefix",false);



        logger.log(Level.INFO,"[TaN] -Loading Local data");
        RegionDataStorage.loadStats();
        PlayerDataStorage.loadStats();
        NewClaimedChunkStorage.loadStats();
        TownDataStorage.loadStats();
        LandmarkStorage.load();
        PlannedAttackStorage.load();
        NewsletterStorage.load();


        logger.log(Level.INFO,"[TaN] -Loading blocks data");
        CustomNBT.setBlocsData();


        logger.log(Level.INFO,"[TaN] -Loading commands");
        SaveStats.startSchedule();
        DailyTasks.scheduleMidnightTask();

        enableEventList();
        getCommand("tan").setExecutor(new PlayerCommandManager());
        getCommand("tanadmin").setExecutor(new AdminCommandManager());
        getCommand("tandebug").setExecutor(new DebugCommandManager());

        setupEconomy();


        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            logger.log(Level.INFO,"[TaN] -Loading PlaceholderAPI");
            new PlaceHolderAPI().register();
        }

        logger.log(Level.INFO,"[TaN] Plugin successfully loaded");

        api = new TanApi();

        new Metrics(this, 20527);
        getLogger().info("\u001B[33m----------------Towns & Nations------------------\u001B[0m");
    }

    /**
     * Method used to set up the economy of the server if Vault is enabled.
     */
    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            logger.log(Level.INFO,"[TaN] -Vault is not detected. Running standalone economy");
            EconomyUtil.setEconomy(new TanEconomyStandalone(), false);
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
            logger.info("[TaN] Not saving data because plugin was closed less than 30s after launch");
            logger.info("[TaN] Plugin disabled");
            return;
        }

        logger.info("[TaN] Savings Data");

        RegionDataStorage.saveStats();
        TownDataStorage.saveStats();
        PlayerDataStorage.saveOldStats();
        PlayerDataStorage.saveStats();
        NewClaimedChunkStorage.save();
        LandmarkStorage.save();
        PlannedAttackStorage.save();
        NewsletterStorage.save();

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        logger.info("[TaN] Plugin disabled");
    }

    /**
     * Enable every event listener of the plugin.
     */
    private void enableEventList() {
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new RareItemDrops(), this);
        getServer().getPluginManager().registerEvents(new RareItemVillagerInteraction(), this);
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
     * Get the plugin Logger
     * @return the plugin logger
     */
    public Logger getPluginLogger() {
        return logger;
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
                    getPluginLogger().log(Level.INFO,"[TaN] A new version is available : {0}", latestVersion);
                } else {
                    getPluginLogger().info("[TaN] Towns and Nation is up to date: "+ CURRENT_VERSION);
                }
            } else {
                getPluginLogger().info("[TaN] An error occurred while trying to accesses github API.");
                getPluginLogger().info("[TaN] Error log : " + con.getInputStream());
            }
        } catch (Exception e) {
            getPluginLogger().warning("[TaN] An error occurred while trying to check for updates.");
        }
    }

    /**
     * Extract the version of the plugin from the response of the GitHub API.
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
     * Get the API of the plugin
     * @return the API of the plugin
     */
    public TanApi getAPI() {
        return api;
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

    /**
     * Notify the plugin that the dynmap addon is loaded and that
     * dynmap features should be enabled.
     * @param dynmapAddonLoaded should be true if dynmap is loaded, false otherwise.
     */
    public void setDynmapAddonLoaded(boolean dynmapAddonLoaded) {
        this.dynmapAddonLoaded = dynmapAddonLoaded;
    }

    /**
     * Check if the dynmap addon is loaded
     * @return true if the dynmap addon is loaded, false otherwise.
     */
    public boolean isDynmapAddonLoaded() {
        return dynmapAddonLoaded;
    }

    public PluginVersion getMinimumSupportingDynmap() {
        return MINIMUM_SUPPORTING_DYNMAP;
    }
}