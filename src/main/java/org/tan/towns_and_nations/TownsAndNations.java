package org.tan.towns_and_nations;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.tan.towns_and_nations.commands.CommandManager;
import org.tan.towns_and_nations.commands.PlayerData.PlayerDataClass;
import org.tan.towns_and_nations.listeners.OnPlayerFirstJoin;
import org.tan.towns_and_nations.listeners.onBedLeaveListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;

public final class TownsAndNations extends JavaPlugin {

    private static TownsAndNations plugin;

    private static List<PlayerDataClass> playerDataClasses;

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("[TaN] Loading Plugin");


        plugin = this;
        //getConfig().options().copyDefaults();

        EnableEventList();
        Objects.requireNonNull(getCommand("tan")).setExecutor(new CommandManager());

        System.out.println("[TaN] Plugin successfully loaded");
    }


    @Override
    public void onDisable() {
        System.out.println("[TaN] Plugin disabled");
    }

    private void EnableEventList(){
        getServer().getPluginManager().registerEvents(new onBedLeaveListener(),this);
        getServer().getPluginManager().registerEvents(new OnPlayerFirstJoin(),this);
    }

    public static TownsAndNations getPlugin(){
        return plugin;
    }


}