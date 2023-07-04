package org.tan.towns_and_nations;


import net.luckperms.api.LuckPerms;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.tan.towns_and_nations.Lang.ChatMessage;
import org.tan.towns_and_nations.commands.CommandManager;
import org.tan.towns_and_nations.commands.DebugCommand;
import org.tan.towns_and_nations.listeners.*;
import org.tan.towns_and_nations.storage.ClaimedChunkStorage;
import org.tan.towns_and_nations.storage.PlayerChatListenerStorage;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;
import org.tan.towns_and_nations.utils.ConfigUtil;

import java.beans.Expression;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;

public final class TownsAndNations extends JavaPlugin {

    private static TownsAndNations plugin;
    static Logger logger;


    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        logger = this.getLogger();
        getLogger().info("\u001B[33m----------------Towns & Nations------------------\u001B[0m");

        logger.info("[TaN] Loading Plugin");

        logger.info("[TaN] -Loading Configs");

        ConfigUtil.loadCustomConfig("config.yml");
        ConfigUtil.saveResource("townLevelUpRequirement.yml");
        ConfigUtil.loadCustomConfig("townLevelUpRequirement.yml");

        logger.info("[TaN] -Loading Lang");
        ChatMessage.loadTranslations();

        logger.info("[TaN] -Loading Stats");

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


    private void EnableEventList() {
        getServer().getPluginManager().registerEvents(new OnPlayerFirstJoin(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new BreakBlockListener(), this);
        getServer().getPluginManager().registerEvents(new VillagerInteractionListener(), this);
        getServer().getPluginManager().registerEvents(new ChunkListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
    }

    public static TownsAndNations getPlugin() {
        return plugin;
    }

    public static Logger getPluginLogger() {
        return logger;
    }




}