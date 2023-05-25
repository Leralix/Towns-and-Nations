package org.tan.towns_and_nations;

import org.bukkit.plugin.java.JavaPlugin;
import org.tan.towns_and_nations.commands.CommandManager;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.listeners.*;
import org.tan.towns_and_nations.utils.PlayerStatStorage;
import org.tan.towns_and_nations.utils.TownDataStorage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public final class TownsAndNations extends JavaPlugin {

    private static TownsAndNations plugin;

    private static List<PlayerDataClass> playerDataClasses;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        System.out.println("[TaN] Loading Plugin");

        try {
            PlayerStatStorage.loadStats();
        } catch (IOException e) {
            System.out.println("[TaN] Error while loading plugin's data");
            throw new RuntimeException(e);
        }
        TownDataStorage.loadStats();


        //getConfig().options().copyDefaults();

        EnableEventList();
        Objects.requireNonNull(getCommand("tan")).setExecutor(new CommandManager());

        System.out.println("[TaN] Plugin successfully loaded");
    }


    @Override
    public void onDisable() {
        System.out.println("[TaN] Savings Data");
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
        System.out.println("[TaN] Plugin disabled");
    }


    private void EnableEventList(){
        getServer().getPluginManager().registerEvents(new onBedLeaveListener(),this);
        getServer().getPluginManager().registerEvents(new OnPlayerFirstJoin(),this);
        getServer().getPluginManager().registerEvents(new GuiListener(),this);
        getServer().getPluginManager().registerEvents(new ChatListener(),this);
        getServer().getPluginManager().registerEvents(new BreakBlockListener(), this);
    }

    public static TownsAndNations getPlugin(){
        return plugin;
    }


}