package org.leralix.tan.events.newsletter;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
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

    private NewsletterStorage() {
        newsletterDAO = new NewsletterDAO(TownsAndNations.getPlugin().getDatabaseHandler().getDataSource());
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

        EventScope scope = newsletter.getType().getBroadcastGlobal();
        if (scope != EventScope.NONE) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                try {
                    if (scope == EventScope.CONCERNED && newsletter.shouldShowToPlayer(player)) {
                        newsletter.broadcastConcerned(player);
                    } else if (scope == EventScope.ALL) {
                        newsletter.broadcast(player);
                    }
                } catch (Exception e) {
                    Bukkit.getLogger().warning("Error while delivering newsletter to " + player.getName() + ": " + e.getMessage());
                    break;
                }
            }
        }

        try {
            newsletterDAO.save(newsletter);
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error while saving newsletter: " + e.getMessage());
        }
    }

    public List<GuiItem> getNewsletterForPlayer(Player player, NewsletterScope scope, Consumer<Player> onClick) {
        List<GuiItem> guis = new ArrayList<>();

        LangType langType = PlayerDataStorage.getInstance().get(player).getLang();

        List<Newsletter> newsletters = newsletterDAO.getNewsletters();

        newsletters.sort(Comparator.comparing(Newsletter::getDate).reversed());

        for (Newsletter newsletter : newsletters) {

            EventScope eventScope = newsletter.getType().getNewsletterScope();

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
        return getNewsletterForPlayer(player, NewsletterScope.SHOW_ONLY_UNREAD, null).size();
    }

    public void clearOldNewsletters() {
        newsletterDAO.deleteOldNewsletters(Constants.getNbDaysBeforeClearningNewsletter());
    }


    public void markAllAsReadForPlayer(Player player) {
        getNewsletterDAO().markAllAsRead(player);
    }


}
