package org.leralix.tan.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.newsletter.NewsletterStorage;
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

        PlayerData playerData = PlayerDataStorage.get(player);


        if(playerData.haveTown()){
            playerData.updateCurrentAttack();
            if(TownsAndNations.getPlugin().townTagIsEnabled())
                prefixUtil.addPrefix(player);
        }

        setIndividualScoreBoard(player);

        if(player.hasPermission("tan.debug") && !TownsAndNations.getPlugin().isLatestVersion()){
            player.sendMessage(getTANString() + Lang.NEW_VERSION_AVAILABLE.get(TownsAndNations.getPlugin().getLatestVersion()));
            player.sendMessage(getTANString() + Lang.NEW_VERSION_AVAILABLE_2.get());
        }

        int nbNewsletterForPlayer = NewsletterStorage.getNbNewsletterForPlayer(playerData);
        if (nbNewsletterForPlayer > 0) {
            player.sendMessage(Lang.NEWSLETTER_STRING.get() + Lang.NEWSLETTER_GREETING.get(nbNewsletterForPlayer));
            TextComponent message = new TextComponent(Lang.CLICK_TO_OPEN_NEWSLETTER.get());
            message.setColor(ChatColor.GOLD);
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tan newsletter"));
            player.spigot().sendMessage(message);
        }
    }
}