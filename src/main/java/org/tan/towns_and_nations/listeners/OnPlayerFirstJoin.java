package org.tan.towns_and_nations.listeners;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.tan.towns_and_nations.TownsAndNations;



public class OnPlayerFirstJoin implements Listener {

    TownsAndNations PluginInstance;

    @EventHandler
    public void onPlayerFirstJoin(PlayerBedEnterEvent event){

        PluginInstance = TownsAndNations.getPlugin();
        System.out.println("AMONGUS");

    }


}
