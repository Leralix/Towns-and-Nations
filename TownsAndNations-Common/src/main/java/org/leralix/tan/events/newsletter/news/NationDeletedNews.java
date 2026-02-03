package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.interfaces.territory.TanNation;
import org.tan.api.interfaces.TanPlayer;

import java.util.UUID;
import java.util.function.Consumer;

public class NationDeletedNews extends Newsletter {

    private final String playerID;
    private final String nationName;

    public NationDeletedNews(TanNation nationData, TanPlayer player) {
        super();
        this.playerID = player.getID().toString();
        this.nationName = nationData.getName();
    }

    public NationDeletedNews(UUID id, long date, String playerID, String oldNationName) {
        super(id, date);
        this.playerID = playerID;
        this.nationName = oldNationName;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getNationName() {
        return nationName;
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.NATION_DELETED;
    }

    @Override
    public void broadcast(Player player, ITanPlayer tanPlayer) {
        TanChatUtils.message(player, Lang.NATION_DELETED_NEWSLETTER.get(tanPlayer, player.getName(), nationName), SoundEnum.GOOD);
    }

    @Override
    public GuiItem createGuiItem(Player player, LangType lang, Consumer<Player> onClick) {
        OfflinePlayer previousLeader = Bukkit.getOfflinePlayer(UUID.fromString(playerID));

        return NewsletterGuiItemUtil.createMarkAsReadGuiItem(
                player,
                lang,
                getDate(),
                IconKey.BROWSE_NATION_ICON,
                Lang.NATION_DELETED_NEWSLETTER_TITLE.get(lang),
                Lang.NATION_DELETED_NEWSLETTER.get(previousLeader.getName(), nationName),
                this::markAsRead,
                onClick
        );
    }

    @Override
    public GuiItem createConcernedGuiItem(Player player, LangType lang, Consumer<Player> onClick) {
        return createGuiItem(player, lang, onClick);
    }

    @Override
    public boolean shouldShowToPlayer(Player player) {
        return true;
    }
}
