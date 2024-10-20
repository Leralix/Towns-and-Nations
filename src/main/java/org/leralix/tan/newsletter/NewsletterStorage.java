package org.leralix.tan.newsletter;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TownData;

import java.util.*;

public class NewsletterStorage {

    private NewsletterStorage() {
        throw new IllegalStateException("Utility class");
    }

    static Map<NewsletterType,NewsletterCategory> categories;

    public static void init() {
        categories = new EnumMap<>(NewsletterType.class);
    }

    public static List<GuiItem> getNewsForPlayer(Player player) {
        if(categories == null)
            init(); // Temporary

        List<GuiItem> newsletters = new ArrayList<>();
        for(NewsletterCategory category : categories.values()) {
            for(Newsletter newsletter : category.getAll()) {
                if(newsletter.shouldShowtoPlayer(player))
                    newsletters.add(newsletter.createGuiItem(player));
            }
        }
        return newsletters;
    }

    public static void registerNewsletter(Newsletter newsletter) {
        if(categories == null)
            init(); // Temporary

        if(!categories.containsKey(newsletter.getType()))
            categories.put(newsletter.getType(), new NewsletterCategory());

        categories.get(newsletter.getType()).add(newsletter);
    }

    public static NewsletterCategory getCategory(NewsletterType type) {
        if(categories == null)
            init(); // Temporary
        return categories.get(type);
    }

    public static void removePlayerJoinRequest(Player player, TownData townData) {
        removePlayerJoinRequest(player.getUniqueId().toString(), townData.getID());
    }
    public static void removePlayerJoinRequest(String playerID, String townID) {
        NewsletterCategory category = categories.get(NewsletterType.PLAYER_TOWN_JOIN_REQUEST);

        if (category == null)
            return;

        category.getAll().removeIf(newsletter ->
                newsletter instanceof PlayerJoinRequestNL &&
                        ((PlayerJoinRequestNL) newsletter).getPlayerID().equals(playerID) &&
                        ((PlayerJoinRequestNL) newsletter).getTownID().equals(townID)
        );
    }
}
