package org.leralix.tan.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.events.newsletter.NewsletterStorage;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.PremiumStorage;
import org.leralix.tan.utils.graphic.PrefixUtil;
import org.leralix.tan.utils.graphic.TeamUtils;
import org.leralix.tan.utils.text.TanChatUtils;


public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);


        if(tanPlayer.hasTown()){
            tanPlayer.updateCurrentAttack();
            PrefixUtil.updatePrefix(player);
        }

        TeamUtils.setIndividualScoreBoard(player);
        LangType langType = tanPlayer.getLang();
        if(player.hasPermission("tan.debug") && !TownsAndNations.getPlugin().isLatestVersion()){
            TanChatUtils.message(player, Lang.NEW_VERSION_AVAILABLE.get(langType, TownsAndNations.getPlugin().getLatestVersion().toString()));
            TanChatUtils.message(player, Lang.NEW_VERSION_AVAILABLE_2.get(langType));
        }

        int nbNewsletterForPlayer = NewsletterStorage.getInstance().getNbUnreadNewsletterForPlayer(player);
        if (nbNewsletterForPlayer > 0) {
            TanChatUtils.message(player, Lang.NEWSLETTER_STRING.get(langType) + Lang.NEWSLETTER_GREETING.get(langType, Integer.toString(nbNewsletterForPlayer)));
            TextComponent message = new TextComponent(Lang.CLICK_TO_OPEN_NEWSLETTER.get(langType));
            message.setColor(ChatColor.GOLD);
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tan newsletter"));
            player.spigot().sendMessage(message);
        }

        Bukkit.getScheduler().runTaskAsynchronously(TownsAndNations.getPlugin(), () -> {
            PremiumStorage.getInstance().isPremium(player.getName());
        });

    }
}