package org.tan.towns_and_nations;


import net.luckperms.api.LuckPerms;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.tan.towns_and_nations.commands.CommandManager;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.commands.DebugCommand;
import org.tan.towns_and_nations.listeners.*;
import org.tan.towns_and_nations.storage.ClaimedChunkStorage;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public final class TownsAndNations extends JavaPlugin {

    private static TownsAndNations plugin;
    static Logger logger;
    FileConfiguration config;


    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        logger = this.getLogger();
        logger.info("[TaN] Loading Plugin");

        //loading config
        this.saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        config.addDefault("test",true);
        config.options().copyDefaults(true);
        saveConfig();

        //Loading data
        PlayerStatStorage.loadStats();
        TownDataStorage.loadStats();
        ClaimedChunkStorage.loadStats();

        //getConfig().options().copyDefaults();

        //API luckperms
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            LuckPerms api = provider.getProvider();
        }


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

    private void loadConfig(){

        File configFile = new File(this.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            System.out.println("pas de fichier config");
            return;
        }

        this.config = YamlConfiguration.loadConfiguration(configFile);

    }

}