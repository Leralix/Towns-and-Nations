package org.tan.TownsAndNations;


import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.tan.TownsAndNations.API.TanAPI;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.Tasks.TaxPayments;
import org.tan.TownsAndNations.Tasks.SaveStats;
import org.tan.TownsAndNations.commands.CommandManager;
import org.tan.TownsAndNations.commands.DebugCommandManager;
import org.tan.TownsAndNations.listeners.*;
import org.tan.TownsAndNations.storage.ClaimedChunkStorage;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;
import org.tan.TownsAndNations.utils.ConfigUtil;
import org.tan.TownsAndNations.utils.DropChances;

import java.util.*;
import java.util.logging.Logger;

public final class TownsAndNations extends JavaPlugin {

    private static TownsAndNations plugin;
    static Logger logger;
    private static TanAPI api;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        logger = this.getLogger();
        getLogger().info("\u001B[33m----------------Towns & Nations------------------\u001B[0m");

        logger.info("[TaN] Loading Plugin");

        logger.info("[TaN] -Loading Configs");
        ConfigUtil.saveResource("config.yml");
        ConfigUtil.loadCustomConfig("config.yml");
        ConfigUtil.saveResource("townLevelUpRequirement.yml");
        ConfigUtil.loadCustomConfig("townLevelUpRequirement.yml");

        DropChances.load();

        logger.info("[TaN] -Loading Lang");
        Lang.loadTranslations("english.yml");
        logger.info(Lang.LANGUAGE_SUCCESSFULLY_LOADED.getTranslation());

        logger.info("[TaN] -Loading Stats");
        PlayerDataStorage.loadStats();
        TownDataStorage.loadStats();
        ClaimedChunkStorage.loadStats();

        logger.info("[TaN] -Loading Scheduled commands");
        SaveStats.startSchedule();
        TaxPayments.scheduleMidnightTask();

        logger.info("[TaN] -Loading API");
        api = new TanAPI();

        EnableEventList();
        Objects.requireNonNull(getCommand("tan")).setExecutor(new CommandManager());
        Objects.requireNonNull(getCommand("tandebug")).setExecutor(new DebugCommandManager());
        
        logger.info("[TaN] Plugin successfully loaded");
        getLogger().info("\u001B[33m----------------Towns & Nations------------------\u001B[0m");

    }


    @Override
    public void onDisable() {
        getLogger().info("\u001B[33m----------------Towns & Nations------------------\u001B[0m");
        logger.info("[TaN] Savings Data");

        TownDataStorage.saveStats();
        ClaimedChunkStorage.saveStats();
        PlayerDataStorage.saveStats();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        logger.info("[TaN] Plugin disabled");
        getLogger().info("\u001B[33m----------------Towns & Nations------------------\u001B[0m");

    }


    private void EnableEventList() {
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new RareItemDrops(), this);
        getServer().getPluginManager().registerEvents(new RareItemVillagerInteraction(), this);
        getServer().getPluginManager().registerEvents(new ChunkListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
    }

    public static TownsAndNations getPlugin() {
        return plugin;
    }

    public static Logger getPluginLogger() {
        return logger;
    }

    public TanAPI getPluginAPI(){
        return api;
    }




}