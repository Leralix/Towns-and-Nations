package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.NationData;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.NationDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.interfaces.TanNation;
import org.tan.api.interfaces.TanPlayer;

import java.util.UUID;
import java.util.function.Consumer;

public class NationCreationNews extends Newsletter {

    private final String playerID;
    private final String nationID;

    public NationCreationNews(TanNation nationData, TanPlayer player) {
        super();
        playerID = player.getUUID().toString();
        nationID = nationData.getID();
    }

    public NationCreationNews(UUID id, long date, String playerID, String nationID) {
        super(id, date);
        this.playerID = playerID;
        this.nationID = nationID;
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.NATION_CREATED;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getNationID() {
        return nationID;
    }

    @Override
    public void broadcast(Player player) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getOrNull(playerID);
        if (tanPlayer == null) {
            return;
        }
        NationData nationData = NationDataStorage.getInstance().get(nationID);
        if (nationData == null) {
            return;
        }
        TanChatUtils.message(player, Lang.NATION_CREATED_NEWSLETTER.get(player, tanPlayer.getNameStored(), nationData.getColoredName()));
    }

    @Override
    public void broadcastConcerned(Player player) {
        broadcast(player);
    }

    @Override
    public GuiItem createGuiItem(Player player, LangType lang, Consumer<Player> onClick) {

        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getOrNull(playerID);
        if (tanPlayer == null) {
            return null;
        }
        NationData nationData = NationDataStorage.getInstance().get(nationID);
        if (nationData == null) {
            return null;
        }

        return NewsletterGuiItemUtil.createMarkAsReadGuiItem(
                player,
                lang,
                getDate(),
                IconKey.BROWSE_NATION_ICON,
                Lang.NATION_CREATED_NEWSLETTER_TITLE.get(lang),
                Lang.NATION_CREATED_NEWSLETTER.get(tanPlayer.getNameStored(), nationData.getColoredName()),
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
