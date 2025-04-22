package org.leralix.tan.newsletter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.triumphteam.gui
.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.newsletter.news.JoinRegionProposalNL;
import org.leralix.tan.newsletter.news.Newsletter;
import org.leralix.tan.newsletter.news.PlayerJoinRequestNL;
import org.leralix.tan.storage.typeadapter.NewsletterAdapter;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class NewsletterStorage {

    private NewsletterStorage() {
        throw new IllegalStateException("Utility class");
    }

    static Map<NewsletterType,List<Newsletter>> categories = new EnumMap<>(NewsletterType.class);

    private static List<Newsletter> getNewsletterForScope(Player player, NewsletterScope scope){
        List<Newsletter> newsletters = new ArrayList<>();
        for(List<Newsletter> category : categories.values()) {
            newsletters.addAll(category);
        }
        return newsletters;
    }

    public static List<GuiItem> getNewsletterForPlayer(Player player, NewsletterScope scope, Consumer<Player> onclick){
        List<GuiItem> newsletters = new ArrayList<>();
        for(Newsletter newsletter : getNewsletterForScope(player, scope)) {
            if(newsletter.shouldShowToPlayer(player, scope)) {
                newsletters.add(newsletter.createGuiItem(player, onclick));
            }
        }
        return newsletters;
    }

    public static void registerNewsletter(Newsletter newsletter) {
        if(!categories.containsKey(newsletter.getType()))
            categories.put(newsletter.getType(), new ArrayList<>());
        categories.get(newsletter.getType()).add(newsletter);
        save();
    }

    public static void removePlayerJoinRequest(PlayerJoinRequestNL playerJoinRequestNL) {
        removePlayerJoinRequest(playerJoinRequestNL.getPlayerID(), playerJoinRequestNL.getTownID());
    }
    public static void removePlayerJoinRequest(PlayerData playerData, TownData townData) {
        removePlayerJoinRequest(playerData.getID(), townData.getID());
    }
    public static void removePlayerJoinRequest(Player player, TownData townData) {
        removePlayerJoinRequest(player.getUniqueId().toString(), townData.getID());
    }
    public static void removePlayerJoinRequest(String playerID, String townID) {
        List<Newsletter> category = categories.get(NewsletterType.PLAYER_TOWN_JOIN_REQUEST);
        if (category == null)
            return;
        category.removeIf(newsletter ->
            newsletter instanceof PlayerJoinRequestNL playerJoinRequestNL &&
                    playerJoinRequestNL.getPlayerID().equals(playerID) &&
                    playerJoinRequestNL.getTownID().equals(townID)
        );
    }

    public static int getNbUnreadNewsletterForPlayer(PlayerData playerData){
        int count = 0;
        for(List<Newsletter> category : categories.values()) {
            for(Newsletter newsletter : category) {
                if(newsletter.shouldShowToPlayer(playerData.getPlayer(), NewsletterScope.SHOW_ONLY_UNREAD))
                    count++;
            }
        }
        return count;
    }

    public static void load() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Newsletter.class, new NewsletterAdapter())
                .setPrettyPrinting()
                .create();

        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Newsletter.json");
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Type type = new TypeToken<EnumMap<NewsletterType, List<Newsletter>>>() {
                }.getType();
                categories = gson.fromJson(reader, type);
            } catch (IOException e) {
                TownsAndNations.getPlugin().getLogger().warning("Error while loading Newsletter file");
            }
        }
    }

    public static void save() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Newsletter.class, new NewsletterAdapter())
                .setPrettyPrinting()
                .create();

        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Newsletter.json");
        file.getParentFile().mkdir();

        try {
            file.createNewFile();
        } catch (IOException e) {
            TownsAndNations.getPlugin().getLogger().warning("Error while creating Newsletter file");
        }

        try (Writer writer = new FileWriter(file, false)) {
            Type type = new TypeToken<Map<NewsletterType, List<Newsletter>>>() {}.getType();
            gson.toJson(categories, type, writer); // Spécifiez le type ici
        } catch (IOException e) {
            TownsAndNations.getPlugin().getLogger().warning("Error while saving Newsletter file");
        }

    }


    public static void clearOldNewsletters() {
        int nbDays = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("TimeBeforeClearingNewsletter");
        long currentTime = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * nbDays; // 1 week
        for(List<Newsletter> category : categories.values()) {
            category.removeIf(newsletter -> newsletter.getDate() < currentTime);
        }
    }

    public static void removeVassalisationProposal(TerritoryData proposer, TerritoryData receiver) {
        List<Newsletter> category = categories.get(NewsletterType.JOIN_REGION_PROPOSAL);
        if (category == null)
            return;
        category.removeIf(newsletter ->
                newsletter instanceof JoinRegionProposalNL proposalNL &&
                        proposalNL.getProposingTerritoryID().equals(proposer.getID()) &&
                        proposalNL.getReceivingTerritoryID().equals(receiver.getID())
        );
    }

    public static void markAllAsReadForPlayer(Player player, NewsletterScope scope) {
        for(Newsletter newsletter : getNewsletterForScope(player, scope)){
            newsletter.markAsRead(player);
        }
    }
}
