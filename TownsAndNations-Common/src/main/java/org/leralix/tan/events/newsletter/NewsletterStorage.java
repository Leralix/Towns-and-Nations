package org.leralix.tan.events.newsletter;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.events.newsletter.dao.NewsletterDAO;
import org.leralix.tan.events.newsletter.news.Newsletter;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class NewsletterStorage {

    private final NewsletterDAO newsletterDAO;

    private static NewsletterStorage instance;

    private final PlayerDataStorage playerDataStorage;

    private NewsletterStorage() {
        newsletterDAO = new NewsletterDAO(TownsAndNations.getPlugin().getDatabaseHandler().getDataSource());
        playerDataStorage = TownsAndNations.getPlugin().getPlayerDataStorage();
    }

    public static NewsletterStorage getInstance() {
        if (instance == null) {
            instance = new NewsletterStorage();
            instance.clearOldNewsletters();
        }
        return instance;
    }


    public NewsletterDAO getNewsletterDAO() {
        return newsletterDAO;
    }

    public void register(Newsletter newsletter) {

        EventScope scope = Constants.getNewsletterScopeConfig().getConfig().get(newsletter.getType()).broadcast();
        if (scope != EventScope.NONE) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                ITanPlayer tanPlayer = playerDataStorage.get(player.getUniqueId());
                if (scope == EventScope.CONCERNED && newsletter.shouldShowToPlayer(player)) {
                    newsletter.broadcastConcerned(player, tanPlayer);
                } else if (scope == EventScope.ALL) {
                    newsletter.broadcast(player, tanPlayer);
                }
            }
        }

        try {
            newsletterDAO.save(newsletter);
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error while saving newsletter: " + e.getMessage());
        }
    }

    public List<GuiItem> getNewsletterForPlayer(Player player, NewsletterScope scope, Consumer<Player> onClick, LangType langType) {
        List<GuiItem> guis = new ArrayList<>();

        List<Newsletter> newsletters = newsletterDAO.getNewsletters();

        newsletters.sort(Comparator.comparing(Newsletter::getDate).reversed());

        for (Newsletter newsletter : newsletters) {

            EventScope eventScope = Constants.getNewsletterScopeConfig().getConfig().get(newsletter.getType()).newsletter();

            if (eventScope == EventScope.NONE) {
                continue;
            }

            if (eventScope == EventScope.CONCERNED && newsletter.shouldShowToPlayer(player)) {
                if (scope == NewsletterScope.SHOW_ALL) {
                    guis.add(newsletter.createConcernedGuiItem(player, langType, onClick));
                } else if (scope == NewsletterScope.SHOW_ONLY_UNREAD && !newsletter.isRead(player)) {
                    guis.add(newsletter.createGuiItem(player, langType, onClick));
                }
            } else if (eventScope == EventScope.ALL
                    && (scope == NewsletterScope.SHOW_ALL
                    || (scope == NewsletterScope.SHOW_ONLY_UNREAD && !newsletter.isRead(player)))) {
                guis.add(newsletter.createGuiItem(player, langType, onClick));
            }

        }
        guis.removeAll(Collections.singleton(null));

        return guis;
    }

    public int getNbUnreadNewsletterForPlayer(Player player) {
        return getNewsletterForPlayer(player, NewsletterScope.SHOW_ONLY_UNREAD, null, LangType.ENGLISH).size();
    }

    public void clearOldNewsletters() {
        newsletterDAO.deleteOldNewsletters(Constants.getNbDaysBeforeClearningNewsletter());
    }


    public void markAllAsReadForPlayer(Player player) {
        getNewsletterDAO().markAllAsRead(player);
    }


}
