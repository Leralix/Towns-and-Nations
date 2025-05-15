package org.leralix.tan.newsletter;

import dev.triumphteam.gui
.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.newsletter.news.TownJoinRegionProposalNews;
import org.leralix.tan.newsletter.news.Newsletter;
import org.leralix.tan.newsletter.news.PlayerJoinRequestNews;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;

import java.util.*;
import java.util.function.Consumer;

public class NewsletterStorage {

    private NewsletterStorage() {
        throw new IllegalStateException("Utility class");
    }

    static Map<NewsletterType,List<Newsletter>> categories = new EnumMap<>(NewsletterType.class);

    public static void register(Newsletter newsletter) {

        EventScope scope = newsletter.getType().getBroadcastGlobal();
        if(scope != EventScope.NONE){
            for(Player player : Bukkit.getOnlinePlayers()){
                try {
                    if(scope == EventScope.ALL || newsletter.shouldShowToPlayer(player)){
                        newsletter.broadcast(player);
                    }
                } catch (Exception e) {
                    Bukkit.getLogger().warning("Error while delivering newsletter to " + player.getName() + ": " + e.getMessage());
                }
            }
        }

        if(!categories.containsKey(newsletter.getType()))
            categories.put(newsletter.getType(), new ArrayList<>());
        categories.get(newsletter.getType()).add(newsletter);
    }

    private static List<Newsletter> getNewsletters(){
        List<Newsletter> newsletters = new ArrayList<>();
        for(List<Newsletter> category : categories.values()) {
            newsletters.addAll(category);
        }
        newsletters.sort(Comparator.comparingLong(Newsletter::getDate).reversed());
        return newsletters;
    }

    public static List<GuiItem> getNewsletterForPlayer(Player player, NewsletterScope scope, Consumer<Player> onClick){
        List<GuiItem> newsletters = new ArrayList<>();


        for(Newsletter newsletter : getNewsletters()) {

            EventScope eventScope = newsletter.getType().getNewsletterScope();

            if(eventScope == EventScope.NONE){
                continue;
            }

            if(eventScope == EventScope.CONCERNED && newsletter.shouldShowToPlayer(player)){
                if(scope == NewsletterScope.SHOW_ALL || !newsletter.isRead(player)){
                    newsletters.add(newsletter.createConcernedGuiItem(player, onClick));
                    continue;
                }
            }
            if(eventScope == EventScope.ALL){
                newsletters.add(newsletter.createGuiItem(player, onClick));
            }
        }
        newsletters.removeAll(Collections.singleton(null));

        return newsletters;
    }

    public static void removePlayerJoinRequest(PlayerJoinRequestNews playerJoinRequestNews) {
        removePlayerJoinRequest(playerJoinRequestNews.getPlayerID(), playerJoinRequestNews.getTownID());
    }
    public static void removePlayerJoinRequest(PlayerData playerData, TownData townData) {
        removePlayerJoinRequest(playerData.getID(), townData.getID());
    }
    public static void removePlayerJoinRequest(Player player, TownData townData) {
        removePlayerJoinRequest(player.getUniqueId().toString(), townData.getID());
    }
    public static void removePlayerJoinRequest(String playerID, String townID) {
        List<Newsletter> category = categories.get(NewsletterType.PLAYER_APPLICATION);
        if (category == null)
            return;
        category.removeIf(newsletter ->
            newsletter instanceof PlayerJoinRequestNews playerJoinRequestNews &&
                    playerJoinRequestNews.getPlayerID().equals(playerID) &&
                    playerJoinRequestNews.getTownID().equals(townID)
        );
    }

    public static int getNbUnreadNewsletterForPlayer(Player player){
        return getNewsletterForPlayer(player, NewsletterScope.SHOW_ONLY_UNREAD, null).size();
    }

    public static void clearOldNewsletters() {
        int nbDays = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("TimeBeforeClearingNewsletter");
        long currentTime = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * nbDays; // 1 week
        for(List<Newsletter> category : categories.values()) {
            category.removeIf(newsletter -> newsletter.getDate() < currentTime);
        }
    }

    public static void removeVassalisationProposal(TerritoryData proposer, TerritoryData receiver) {
        List<Newsletter> category = categories.get(NewsletterType.TOWN_JOIN_REGION_PROPOSAL);
        if (category == null)
            return;
        category.removeIf(newsletter ->
                newsletter instanceof TownJoinRegionProposalNews proposalNL &&
                        proposalNL.getProposingTerritoryID().equals(proposer.getID()) &&
                        proposalNL.getReceivingTerritoryID().equals(receiver.getID())
        );
    }

    public static void markAllAsReadForPlayer(Player player, NewsletterScope scope) {
        for(Newsletter newsletter : getNewsletters()){
            newsletter.markAsRead(player);
        }
    }

    public static void load() {

    }

    public static void save() {

    }
}
