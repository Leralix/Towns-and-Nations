package org.leralix.tan.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
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

        PlayerDataStorage.getInstance().get(player).thenAccept(tanPlayer -> {
            // All player interactions MUST run on the main thread (Folia global region scheduler)
            org.leralix.tan.utils.FoliaScheduler.runTask(TownsAndNations.getPlugin(), () -> {
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

                NewsletterStorage.getInstance().getNbUnreadNewsletterForPlayer(player).thenAccept(nbNewsletterForPlayer -> {
                    if (nbNewsletterForPlayer > 0) {
                        TanChatUtils.message(player, Lang.NEWSLETTER_STRING.get(langType) + Lang.NEWSLETTER_GREETING.get(langType, Integer.toString(nbNewsletterForPlayer)));
                        Component message = Component.text(Lang.CLICK_TO_OPEN_NEWSLETTER.get(langType))
                                .color(NamedTextColor.GOLD)
                                .clickEvent(ClickEvent.runCommand("/tan newsletter"));
                        player.sendMessage(message);
                    }
                });

                // Check premium status asynchronously
                org.leralix.tan.utils.FoliaScheduler.runTaskAsynchronously(TownsAndNations.getPlugin(), () -> {
                    PremiumStorage.getInstance().isPremium(player.getName());
                });
            });
        }).exceptionally(ex -> {
            TownsAndNations.getPlugin().getLogger().severe("Error retrieving player data for " + player.getName() + ": " + ex.getMessage());
            return null;
        });
    }
}
