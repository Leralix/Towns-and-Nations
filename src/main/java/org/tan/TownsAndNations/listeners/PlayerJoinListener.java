package org.tan.TownsAndNations.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.TeamUtils;


public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();


        if (PlayerDataStorage.get(player) == null) {
            Bukkit.broadcastMessage(ChatUtils.getTANString() + player.getName() + " a rejoint le serveur pour la première fois !");
            PlayerDataStorage.createPlayerDataClass(player);
        }

        PlayerData playerStat = PlayerDataStorage.get(player);
        if(playerStat.getTownId() != null)
            TeamUtils.setScoreBoard(player);

        if(player.hasPermission("tan.debug"))
            player.sendMessage(ChatUtils.getTANDebugString() + Lang.WELCOME.getTranslation());

    }
}