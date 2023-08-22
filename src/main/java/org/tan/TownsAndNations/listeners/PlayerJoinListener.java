package org.tan.TownsAndNations.listeners;

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
    TownsAndNations PluginInstance;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();


        if (PlayerDataStorage.get(player) == null) {
            PlayerDataStorage.createPlayerDataClass(player);
            //PluginInstance = TownsAndNations.getPlugin();
            //PluginInstance.getServer().broadcastMessage(player.getName() + "à rejoint le serveur pour la premiere fois");
        }

        PlayerData playerStat = PlayerDataStorage.get(player);
        if(playerStat.getTownId() != null)
            TeamUtils.setScoreBoard(player);


        player.sendMessage(ChatUtils.getTANDebugString() + Lang.WELCOME.getTranslation());

    }
}