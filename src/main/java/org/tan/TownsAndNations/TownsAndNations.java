package org.tan.TownsAndNations;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.tan.TownsAndNations.API.tanAPI;
import org.tan.TownsAndNations.Bstats.Metrics;
import org.tan.TownsAndNations.Lang.DynamicLang;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.Tasks.DailyTasks;
import org.tan.TownsAndNations.Tasks.SaveStats;
import org.tan.TownsAndNations.commands.AdminCommandManager;
import org.tan.TownsAndNations.commands.CommandManager;
import org.tan.TownsAndNations.commands.DebugCommandManager;
import org.tan.TownsAndNations.listeners.*;
import org.tan.TownsAndNations.storage.*;
import org.tan.TownsAndNations.utils.ConfigUtil;
import org.tan.TownsAndNations.utils.DropChances;
import org.tan.TownsAndNations.utils.UpdateUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Logger;

public final class TownsAndNations extends JavaPlugin {

    private static TownsAndNations plugin;
    static Logger logger;
    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GITHUB_API_URL = "https://api.github.com/repos/leralix/towns-and-nations/releases/latest";
    private static String LOADED_VERSION;
    private static final String CURRENT_VERSION = "v0.5.4";
    private static String LATEST_VERSION;
    private static tanAPI api;
    private static boolean allowColorCodes = false;
    private static boolean sqlEnable = false;
    private static boolean dynmapAddonLoaded = true;
    private static boolean autoUpdateLangFiles = true;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        logger = this.getLogger();
        getLogger().info("\u001B[33m----------------Towns & Nations------------------\u001B[0m");
        getLogger().info("To report a bug or request a feature, please ask on my discord server: https://discord.gg/Q8gZSFUuzb");

        logger.info("[TaN] Loading Plugin");

        checkForUpdate();

        logger.info("[TaN] -Loading Lang");

        ConfigUtil.saveResource("lang.yml");
        ConfigUtil.loadCustomConfig("lang.yml");
        String lang = ConfigUtil.getCustomConfig("lang.yml").getString("language","en");

        if(lang.contains(".yml")){ // Old lang config needed the .yml, not needed anymore
            lang = lang.substring(0,2);
        }

        Lang.loadTranslations(lang + ".yml");
        DynamicLang.loadTranslations(lang + "-upgrades.yml");


        logger.info("[TaN] -Loading Configs");
        ConfigUtil.saveAndUpdateResource("config.yml");
        ConfigUtil.loadCustomConfig("config.yml");
        ConfigUtil.saveAndUpdateResource("townLevelUpRequirement.yml");
        ConfigUtil.loadCustomConfig("townLevelUpRequirement.yml");
        ConfigUtil.saveAndUpdateResource("townUpgrades.yml");
        ConfigUtil.loadCustomConfig("townUpgrades.yml");

        DropChances.load();
        UpgradeStorage.init();
        MobChunkSpawnStorage.init();
        autoUpdateLangFiles = ConfigUtil.getCustomConfig("config.yml").getBoolean("AutoUpdateLangFiles", true);
        allowColorCodes = ConfigUtil.getCustomConfig("config.yml").getBoolean("EnablePlayerColorCode", false);

        sqlEnable = ConfigUtil.getCustomConfig("config.yml").getBoolean("EnableCrossServer", false);
        if(sqlEnable){
            logger.info("[TaN] -Loading SQL connections");

            String host = ConfigUtil.getCustomConfig("config.yml").getString("SQL.address");
            String username = ConfigUtil.getCustomConfig("config.yml").getString("SQL.username");
            String password = ConfigUtil.getCustomConfig("config.yml").getString("SQL.password");


            PlayerDataStorage.initialize(host,username,password);
            TownDataStorage.initialize(host,username,password);
        }
        else{
            logger.info("[TaN] -Loading Local data");
            RegionDataStorage.loadStats();
            PlayerDataStorage.loadStats();
            TownDataStorage.loadStats();
            ClaimedChunkStorage.loadStats(); // Used for v0.5.4 -> v0.6.0 update
            NewClaimedChunkStorage.loadStats();
        }

        UpdateUtil.update();


        logger.info("[TaN] -Loading commands");
        SaveStats.startSchedule();
        DailyTasks.scheduleMidnightTask();

        EnableEventList();
        Objects.requireNonNull(getCommand("tan")).setExecutor(new CommandManager());
        Objects.requireNonNull(getCommand("tanadmin")).setExecutor(new AdminCommandManager());
        Objects.requireNonNull(getCommand("tandebug")).setExecutor(new DebugCommandManager());

        if (setupEconomy()) {
            logger.info("[TaN] -Vault API successfully loaded");
            setupPermissions();
            setupChat();
        } else {
            logger.info("[TaN] -Vault API not found, using own economy system");
        }


        UpdateUtil.updateDatabase();

        /*
        if (!Objects.equals(LOADED_VERSION, CURRENT_VERSION)){
            LOADED_VERSION = CURRENT_VERSION;
            logger.info("[TaN] Plugin updated to version " + CURRENT_VERSION + ". If an error occurs, please report it on the discord server.");
            UpdateUtil.UpdateToNewVersion();
        }
        */

        logger.info("[TaN] Plugin successfully loaded");

        api = new tanAPI();

        int pluginId = 20527; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);
        getLogger().info("\u001B[33m----------------Towns & Nations------------------\u001B[0m");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private void setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if(rsp == null){
            return;
        }
        perms = rsp.getProvider();
    }

    private void setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        if(rsp == null){
            return;
        }
        chat = rsp.getProvider();
    }



    @Override
    public void onDisable() {
        if(!isSqlEnable()){
            logger.info("[TaN] Savings Data");

            RegionDataStorage.saveStats();
            TownDataStorage.saveStats();
            PlayerDataStorage.saveStats();
            NewClaimedChunkStorage.saveStats();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        logger.info("[TaN] Plugin disabled");
    }


    private void EnableEventList() {
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new RareItemDrops(), this);
        getServer().getPluginManager().registerEvents(new RareItemVillagerInteraction(), this);
        getServer().getPluginManager().registerEvents(new ChunkListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerEnterChunkListener(), this);
        getServer().getPluginManager().registerEvents(new ChatScopeListener(), this);
        getServer().getPluginManager().registerEvents(new MobSpawnListener(), this);
    }

    public static TownsAndNations getPlugin() {
        return plugin;
    }

    public static Logger getPluginLogger() {
        return logger;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static boolean hasEconomy(){
        return econ != null;
    }

    private void checkForUpdate() {
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

                LATEST_VERSION = extractVersionFromResponse(response.toString());
                if (!CURRENT_VERSION.equals(LATEST_VERSION)) {
                    getPluginLogger().info("[TaN] A new version is available : " + LATEST_VERSION);
                } else {
                    getPluginLogger().info("[TaN] Towns and Nation is up to date: "+ CURRENT_VERSION);
                }
            } else {
                getPluginLogger().info("[TaN] An error occurred while trying to accesses github API.");
                getPluginLogger().info("[TaN] Error log : " + con.getInputStream());
            }
        } catch (Exception e) {
            getPluginLogger().warning("[TaN] An error occurred while trying to check for updates.");
            e.printStackTrace();
        }
    }

    private String extractVersionFromResponse(String response) {
        JsonParser parser = new JsonParser();
        JsonObject jsonResponse = parser.parse(response).getAsJsonObject();
        return jsonResponse.get("name").getAsString();
    }

    public static boolean isLatestVersion(){
        return CURRENT_VERSION.equals(LATEST_VERSION);
    }

    public static String getLatestVersion(){
        return LATEST_VERSION;
    }

    public static tanAPI getAPI() {
        return api;
    }
    public static boolean colorCodeIsNotEnabled(){
        return !allowColorCodes;
    }

    public static boolean isSqlEnable() {
        return sqlEnable;
    }

    public static void setDynmapAddonLoaded(boolean dynmapAddonLoaded) {
        TownsAndNations.dynmapAddonLoaded = dynmapAddonLoaded;
    }

    public static boolean isDynmapAddonLoaded() {
        return dynmapAddonLoaded;
    }

}