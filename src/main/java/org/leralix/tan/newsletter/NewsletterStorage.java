package org.leralix.tan.newsletter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.typeadapter.NewsletterAdapter;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class NewsletterStorage {

    private NewsletterStorage() {
        throw new IllegalStateException("Utility class");
    }

    static Map<NewsletterType,List<Newsletter>> categories;

    public static List<GuiItem> getNewsletterForPlayer(Player player) {
        List<GuiItem> newsletters = new ArrayList<>();
        for(List<Newsletter> category : categories.values()) {
            for(Newsletter newsletter : category) {
                if(newsletter.shouldShowToPlayer(player))
                    newsletters.add(newsletter.createGuiItem(player));
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
            newsletter instanceof PlayerJoinRequestNL &&
                    ((PlayerJoinRequestNL) newsletter).getPlayerID().equals(playerID) &&
                    ((PlayerJoinRequestNL) newsletter).getTownID().equals(townID)
        );
    }

    public static void load() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Newsletter.class, new NewsletterAdapter())
                .setPrettyPrinting()
                .create();

        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Newsletter.json");
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Type type = new TypeToken<EnumMap<NewsletterType, List<Newsletter>>>() {}.getType();
                categories = gson.fromJson(reader, type);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (categories == null) {
            categories = new EnumMap<>(NewsletterType.class);
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
            throw new RuntimeException(e);
        }

        try (Writer writer = new FileWriter(file, false)) {
            Type type = new TypeToken<Map<NewsletterType, List<Newsletter>>>() {}.getType();
            gson.toJson(categories, type, writer); // Spécifiez le type ici
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static int getNbNewsletterForPlayer(PlayerData playerData){
        int count = 0;
        for(List<Newsletter> category : categories.values()) {
            for(Newsletter newsletter : category) {
                if(newsletter.shouldShowToPlayer(playerData.getPlayer()))
                    count++;
            }
        }
        return count;
    }
}
