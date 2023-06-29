package org.tan.towns_and_nations;


import net.luckperms.api.LuckPerms;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.tan.towns_and_nations.commands.CommandManager;
import org.tan.towns_and_nations.commands.DebugCommand;
import org.tan.towns_and_nations.listeners.*;
import org.tan.towns_and_nations.storage.ClaimedChunkStorage;
import org.tan.towns_and_nations.storage.PlayerChatListenerStorage;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;

import java.beans.Expression;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;

public final class TownsAndNations extends JavaPlugin {

    private static TownsAndNations plugin;
    static Logger logger;
    private static final Map<String, FileConfiguration> configs = new HashMap<>();


    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        logger = this.getLogger();
        getLogger().info("\u001B[33mTowns & Nations.\u001B[0m");

        logger.info("[TaN] Loading Plugin");

        logger.info("[TaN] Loading Configs");

        loadCustomConfig("config.yml");
        loadCustomConfig("townLevelUpRequirement.yml");

        logger.info("[TaN] Loading Stats");

        //Loading data
        PlayerStatStorage.loadStats();
        TownDataStorage.loadStats();
        ClaimedChunkStorage.loadStats();
        PlayerChatListenerStorage.load();


        //API luckperms
        /*
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            LuckPerms api = provider.getProvider();
        }
        */

        EnableEventList();
        Objects.requireNonNull(getCommand("tan")).setExecutor(new CommandManager());
        Objects.requireNonNull(getCommand("tandebug")).setExecutor(new DebugCommand());

        logger.info("[TaN] Plugin successfully loaded");
    }


    @Override
    public void onDisable() {
        logger.info("[TaN] Savings Data");

        ClaimedChunkStorage.saveStats();
        PlayerStatStorage.saveStats();
        ClaimedChunkStorage.saveStats();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        logger.info("[TaN] Plugin disabled");
    }


    private void EnableEventList(){
        getServer().getPluginManager().registerEvents(new OnPlayerFirstJoin(),this);
        getServer().getPluginManager().registerEvents(new ChatListener(),this);
        getServer().getPluginManager().registerEvents(new BreakBlockListener(), this);
        getServer().getPluginManager().registerEvents(new VillagerInteractionListener(), this);
        getServer().getPluginManager().registerEvents(new ChunkListener(),this);
    }

    public static TownsAndNations getPlugin(){
        return plugin;
    }

    public static Logger getPluginLogger(){return logger;}

    public void loadCustomConfig(String fileName) {
        File configFile = new File(getDataFolder(), fileName);
        if (!configFile.exists()) {
            getLogger().severe(fileName + " does not exist!");
            return;
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        configs.put(fileName, config);
    }

    public static FileConfiguration getCustomConfig(String fileName) {
        return configs.get(fileName);
    }


}