package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.events.newsletter.NewsletterStorage;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.lang.LangType;

import java.util.UUID;
import java.util.function.Consumer;

public abstract class Newsletter {

    private final UUID id;
    private final long date;

    protected Newsletter(UUID id, long date) {
        this.id = id;
        this.date = date;
    }

    protected Newsletter() {
        this.id = UUID.randomUUID();
        this.date = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public abstract GuiItem createGuiItem(Player player, LangType lang, Consumer<Player> onClick);

    public abstract GuiItem createConcernedGuiItem(Player player, LangType lang, Consumer<Player> onClick);

    public abstract boolean shouldShowToPlayer(Player player);

    public long getDate() {
        return date;
    }

    public abstract NewsletterType getType();

    public void markAsRead(Player player){
        markAsRead(player.getUniqueId());
    }

    public void markAsRead(ITanPlayer tanPlayer){
        markAsRead(tanPlayer.getID());
    }

    public void markAsRead(UUID playerID){
        NewsletterStorage.getInstance().getNewsletterDAO().markAsRead(id, playerID);
    }

    public boolean isRead(Player player){
        return isRead(player.getUniqueId());
    }

    public boolean isRead(UUID playerID) {
       return NewsletterStorage.getInstance().getNewsletterDAO().hasRead(id, playerID);
    }

    public abstract void broadcast(Player player);

    public abstract void broadcastConcerned(Player player);

}
