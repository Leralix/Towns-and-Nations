package org.leralix.tan.newsletter.storage;

import dev.triumphteam.gui
.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.newsletter.EventScope;
import org.leralix.tan.newsletter.NewsletterScope;
import org.leralix.tan.newsletter.news.Newsletter;
import org.leralix.tan.newsletter.news.PlayerJoinRequestNews;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.storage.database.DatabaseHandler;

import java.util.*;
import java.util.function.Consumer;

public class NewsletterStorage {

    private NewsletterStorage() {
        throw new IllegalStateException("Utility class");
    }

    private static NewsletterDAO newsletterDAO;

    public static void init() {

        DatabaseHandler databaseHandler = TownsAndNations.getPlugin().getDatabaseHandler();

        newsletterDAO = new NewsletterDAO(databaseHandler.getDataSource());
    }

    public static NewsletterDAO getNewsletterDAO() {
        return newsletterDAO;
    }

    public static void register(Newsletter newsletter) {

        EventScope scope = newsletter.getType().getBroadcastGlobal();
        if(scope != EventScope.NONE){
            for(Player player : Bukkit.getOnlinePlayers()){
                try {
                    if(scope == EventScope.CONCERNED && newsletter.shouldShowToPlayer(player)){
                        newsletter.broadcastConcerned(player);
                    }
                    else if(scope == EventScope.ALL){
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

    private static List<Newsletter> getNewsletters(){
        return newsletterDAO.getNewsletters();
    }

    public static List<GuiItem> getNewsletterForPlayer(Player player, NewsletterScope scope, Consumer<Player> onClick){
        List<GuiItem> newsletters = new ArrayList<>();


        for(Newsletter newsletter : getNewsletters()) {

            EventScope eventScope = newsletter.getType().getNewsletterScope();

            if(eventScope == EventScope.NONE){
                continue;
            }

            if(eventScope == EventScope.CONCERNED && newsletter.shouldShowToPlayer(player)){
                if(scope == NewsletterScope.SHOW_ALL){
                    newsletters.add(newsletter.createConcernedGuiItem(player, onClick));
                    continue;
                }
                if(scope == NewsletterScope.SHOW_ONLY_UNREAD && !newsletter.isRead(player)){
                    newsletters.add(newsletter.createGuiItem(player, onClick));
                    continue;
                }
            }
            if(eventScope == EventScope.ALL){
                if(scope == NewsletterScope.SHOW_ALL){
                    newsletters.add(newsletter.createGuiItem(player, onClick));
                    continue;
                }
                if(scope == NewsletterScope.SHOW_ONLY_UNREAD && !newsletter.isRead(player)){
                    newsletters.add(newsletter.createGuiItem(player, onClick));
                    continue;
                }
            }
        }
        newsletters.removeAll(Collections.singleton(null));

        return newsletters;
    }

    public static void removePlayerJoinRequest(PlayerJoinRequestNews playerJoinRequestNews) {
        removePlayerJoinRequest(playerJoinRequestNews.getPlayerID(), playerJoinRequestNews.getTownID());
    }
    public static void removePlayerJoinRequest(ITanPlayer ITanPlayer, TownData townData) {
        removePlayerJoinRequest(ITanPlayer.getID(), townData.getID());
    }
    public static void removePlayerJoinRequest(Player player, TownData townData) {
        removePlayerJoinRequest(player.getUniqueId().toString(), townData.getID());
    }
    public static void removePlayerJoinRequest(String playerID, String townID) {

    }

    public static int getNbUnreadNewsletterForPlayer(Player player){
        return getNewsletterForPlayer(player, NewsletterScope.SHOW_ONLY_UNREAD, null).size();
    }

    public static void clearOldNewsletters() {
        int nbDays = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("TimeBeforeClearingNewsletter");
        long currentTime = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * nbDays;

    }

    public static void removeVassalisationProposal(TerritoryData proposer, TerritoryData receiver) {

    }

    public static void markAllAsReadForPlayer(Player player, NewsletterScope scope) {
        for(Newsletter newsletter : getNewsletters()){
            newsletter.markAsRead(player);
        }
    }

}
