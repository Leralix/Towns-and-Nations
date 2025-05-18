package org.leralix.tan.newsletter.news;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.newsletter.NewsletterType;
import org.leralix.tan.newsletter.storage.NewsletterStorage;

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

    public abstract GuiItem createGuiItem(Player player, Consumer<Player> onClick);

    public abstract GuiItem createConcernedGuiItem(Player player, Consumer<Player> onClick);

    public abstract boolean shouldShowToPlayer(Player player);

    public long getDate() {
        return date;
    }

    public abstract NewsletterType getType();

    public void markAsRead(Player player){
        markAsRead(player.getUniqueId());
    }

    public void markAsRead(PlayerData playerData){
        markAsRead(UUID.fromString(playerData.getID()));
    }

    public void markAsRead(UUID playerID){
        NewsletterStorage.getNewsletterDAO().markAsRead(id, playerID);
    }

    public boolean isRead(Player player){
        return isRead(player.getUniqueId());
    }

    public boolean isRead(UUID playerID) {
       return NewsletterStorage.getNewsletterDAO().hasRead(id, playerID);
    }

    public abstract void broadcast(Player player);


}
