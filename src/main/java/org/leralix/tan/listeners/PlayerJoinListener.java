package org.leralix.tan.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.prefixUtil;

import static org.leralix.tan.enums.TownRolePermission.INVITE_PLAYER;
import static org.leralix.tan.utils.ChatUtils.getTANString;
import static org.leralix.tan.utils.TeamUtils.setIndividualScoreBoard;


public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PlayerData playerStat = PlayerDataStorage.get(player);


        if(playerStat.haveTown()){
            if(!TownDataStorage.get(playerStat).getPlayerJoinRequestSet().isEmpty() && playerStat.hasPermission(INVITE_PLAYER)){
                player.sendMessage(
                        Lang.NEWSLETTER_STRING.get() +
                        Lang.GUI_TOWN_MEMBERS_MANAGE_APPLICATION_DESC1.get(TownDataStorage.get(playerStat).getPlayerJoinRequestSet().size())
                );
            }
            playerStat.updateCurrentAttack();
            if(TownsAndNations.townTagIsEnabled())
                prefixUtil.addPrefix(player);
        }

        setIndividualScoreBoard(player);

        if(player.hasPermission("tan.debug"))
            if(!TownsAndNations.isLatestVersion()){
                player.sendMessage(getTANString() + Lang.NEW_VERSION_AVAILABLE.get(TownsAndNations.getLatestVersion()));
                player.sendMessage(getTANString() + Lang.NEW_VERSION_AVAILABLE_2.get());
            }

    }
}