package org.tan.towns_and_nations.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.Lang.Lang;
import org.tan.towns_and_nations.TownsAndNations;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.utils.ChatUtils;
import org.tan.towns_and_nations.utils.TeamUtils;


public class PlayerJoinListener implements Listener {
    TownsAndNations PluginInstance;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();


        if (PlayerStatStorage.getStat(player) == null) {
            PlayerStatStorage.createPlayerDataClass(player);
            //PluginInstance = TownsAndNations.getPlugin();
            //PluginInstance.getServer().broadcastMessage(player.getName() + "à rejoint le serveur pour la premiere fois");
        }

        PlayerDataClass playerStat = PlayerStatStorage.getStat(player);
        if(playerStat.getTownId() != null)
            TeamUtils.setScoreBoard(player);


        player.sendMessage(ChatUtils.getTANDebugString() + Lang.WELCOME.getTranslation());

    }
}