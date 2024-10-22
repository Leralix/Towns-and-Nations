package org.leralix.tan.newsletter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TownData;

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

    static Map<NewsletterType,NewsletterCategory> categories;

    public static List<GuiItem> getNewsletterForPlayer(Player player) {
        List<GuiItem> newsletters = new ArrayList<>();
        for(NewsletterCategory category : categories.values()) {
            for(Newsletter newsletter : category.getAll()) {
                if(newsletter.shouldShowToPlayer(player))
                    newsletters.add(newsletter.createGuiItem(player));
            }
        }
        return newsletters;
    }

    public static void registerNewsletter(Newsletter newsletter) {
        if(!categories.containsKey(newsletter.getType()))
            categories.put(newsletter.getType(), new NewsletterCategory());
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
        NewsletterCategory category = categories.get(NewsletterType.PLAYER_TOWN_JOIN_REQUEST);

        if (category == null)
            return;

        category.getAll().removeIf(newsletter ->
            newsletter instanceof PlayerJoinRequestNL &&
                    ((PlayerJoinRequestNL) newsletter).getPlayerID().equals(playerID) &&
                    ((PlayerJoinRequestNL) newsletter).getTownID().equals(townID)
        );
    }

    public static void load() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Newsletter.json");
        if (file.exists()){
            Reader reader;
            try {
                reader = new FileReader(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            Type type = new TypeToken<EnumMap<NewsletterType,NewsletterCategory>>() {}.getType();
            categories = gson.fromJson(reader, type);
        }
        if (categories == null)
            categories = new EnumMap<>(NewsletterType.class);
    }

    public static void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Newsletter.json");
        file.getParentFile().mkdir();

        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Writer writer;
        try {
            writer = new FileWriter(file, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gson.toJson(categories, writer);
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getNbNewsletterForPlayer(PlayerData playerData){
        int count = 0;
        for(NewsletterCategory category : categories.values()) {
            for(Newsletter newsletter : category.getAll()) {
                if(newsletter.shouldShowToPlayer(playerData.getPlayer()))
                    count++;
            }
        }
        return count;
    }
}
