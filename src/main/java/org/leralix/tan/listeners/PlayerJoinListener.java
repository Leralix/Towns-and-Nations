package org.leralix.tan.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.events.newsletter.NewsletterStorage;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.utils.TeamUtils;
import org.leralix.tan.utils.prefixUtil;


public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);


        if(tanPlayer.hasTown()){
            tanPlayer.updateCurrentAttack();
            if(TownsAndNations.getPlugin().townTagIsEnabled())
                prefixUtil.addPrefix(player);
        }

        TeamUtils.setIndividualScoreBoard(player);

        if(player.hasPermission("tan.debug") && !TownsAndNations.getPlugin().isLatestVersion()){
            player.sendMessage(TanChatUtils.getTANString() + Lang.NEW_VERSION_AVAILABLE.get(TownsAndNations.getPlugin().getLatestVersion()));
            player.sendMessage(TanChatUtils.getTANString() + Lang.NEW_VERSION_AVAILABLE_2.get());
        }

        int nbNewsletterForPlayer = NewsletterStorage.getInstance().getNbUnreadNewsletterForPlayer(player);
        if (nbNewsletterForPlayer > 0) {
            player.sendMessage(Lang.NEWSLETTER_STRING.get() + Lang.NEWSLETTER_GREETING.get(nbNewsletterForPlayer));
            TextComponent message = new TextComponent(Lang.CLICK_TO_OPEN_NEWSLETTER.get());
            message.setColor(ChatColor.GOLD);
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tan newsletter"));
            player.spigot().sendMessage(message);
        }
    }
}