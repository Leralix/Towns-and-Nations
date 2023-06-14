package org.tan.towns_and_nations;


import net.luckperms.api.LuckPerms;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.tan.towns_and_nations.commands.CommandManager;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.commands.DebugCommand;
import org.tan.towns_and_nations.listeners.*;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public final class TownsAndNations extends JavaPlugin {

    private static TownsAndNations plugin;

    private static List<PlayerDataClass> playerDataClasses;
    static Logger logger;


    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        logger = this.getLogger();
        logger.info("[TaN] Loading Plugin");

        //Loading data
        try {
            PlayerStatStorage.loadStats();
        } catch (IOException e) {
            System.out.println("[TaN] Error while loading plugin's data");
            throw new RuntimeException(e);
        }
        TownDataStorage.loadStats();

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
        try {
            PlayerStatStorage.saveStats();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
}