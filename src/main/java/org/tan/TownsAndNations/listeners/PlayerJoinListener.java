package org.tan.TownsAndNations.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;

import static org.tan.TownsAndNations.enums.TownRolePermission.INVITE_PLAYER;
import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;
import static org.tan.TownsAndNations.utils.TeamUtils.setIndividualScoreBoard;


public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PlayerData playerStat = PlayerDataStorage.get(player);

        setIndividualScoreBoard(player);

        if(playerStat.haveTown()){
            if(!TownDataStorage.get(playerStat).getPlayerJoinRequestSet().isEmpty()  && playerStat.hasPermission(INVITE_PLAYER)){
                player.sendMessage(
                        Lang.NEWSLETTER_STRING.get() +
                        Lang.GUI_TOWN_MEMBERS_MANAGE_APPLICATION_DESC1.get(TownDataStorage.get(playerStat).getPlayerJoinRequestSet().size())
                );
            }
        }


        if(player.hasPermission("tan.debug"))
            if(!TownsAndNations.isLatestVersion()){
                player.sendMessage(getTANString() + Lang.NEW_VERSION_AVAILABLE.get(TownsAndNations.getLatestVersion()));
                player.sendMessage(getTANString() + Lang.NEW_VERSION_AVAILABLE_2.get());
            }

    }
}