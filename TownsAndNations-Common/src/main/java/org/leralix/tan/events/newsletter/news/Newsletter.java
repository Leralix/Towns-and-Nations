package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.events.newsletter.NewsletterStorage;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.DateUtil;

import java.util.Collection;
import java.util.List;
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

    public abstract void broadcast(Player player, ITanPlayer tanPlayer);

    /**
     * Defines if the newsletter should be broadcasted to the player.
     * By default, it calls the broadcast method (everyone is concerned).
     * @param player        The player to check.
     * @param playerData    The player data.
     */
    public void broadcastConcerned(Player player, ITanPlayer playerData){
        broadcast(player, playerData);
    }

    protected GuiItem createBasicNewsletter(
            Material material,
            FilledLang title,
            FilledLang fillDescription,
            LangType lang,
            Consumer<Player> onClick,
            Player player
    ) {
        ItemStack icon = HeadUtils.createCustomItemStack(material,
                title.get(lang),
                Lang.NEWSLETTER_DATE.get(lang, DateUtil.getRelativeTimeDescription(lang, getDate())),
                fillDescription.get(lang),
                Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ.get(lang));

        return ItemBuilder.from(icon).asGuiItem(event -> {
            event.setCancelled(true);
            if (event.isRightClick()) {
                markAsRead(player);
                onClick.accept(player);
            }
        });
    }
}
