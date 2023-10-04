package org.tan.TownsAndNations.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.enums.TownRolePermission;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.TeamUtils;


public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();


        if (PlayerDataStorage.get(player) == null) {
            PlayerDataStorage.createPlayerDataClass(player);
        }


        PlayerData playerStat = PlayerDataStorage.get(player);
        if(playerStat.getTownId() != null)
            TeamUtils.setScoreBoard(player);

        if(!TownDataStorage.get(playerStat).getPlayerJoinRequestSet().isEmpty()){
            if(playerStat.hasPermission(TownRolePermission.INVITE_PLAYER)){
                player.sendMessage(
                        Lang.NEWSLETTER_STRING.getTranslation() +
                                Lang.GUI_TOWN_MEMBERS_MANAGE_APPLICATION_DESC1.getTranslation(TownDataStorage.get(playerStat).getPlayerJoinRequestSet().size())
                );
            }
        }


        if(player.hasPermission("tan.debug"))
            player.sendMessage(ChatUtils.getTANDebugString() + Lang.WELCOME.getTranslation());

    }
}