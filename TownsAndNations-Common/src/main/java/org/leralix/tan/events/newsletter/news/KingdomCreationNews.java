package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.KingdomData;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.KingdomDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.DateUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.interfaces.TanKingdom;
import org.tan.api.interfaces.TanPlayer;

import java.util.UUID;
import java.util.function.Consumer;

public class KingdomCreationNews extends Newsletter {

    private final String playerID;
    private final String kingdomID;

    public KingdomCreationNews(TanKingdom kingdomData, TanPlayer player) {
        super();
        playerID = player.getUUID().toString();
        kingdomID = kingdomData.getID();
    }

    public KingdomCreationNews(UUID id, long date, String playerID, String kingdomID) {
        super(id, date);
        this.playerID = playerID;
        this.kingdomID = kingdomID;
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.KINGDOM_CREATED;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getKingdomID() {
        return kingdomID;
    }

    @Override
    public void broadcast(Player player) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(playerID);
        if (tanPlayer == null) {
            return;
        }
        KingdomData kingdomData = KingdomDataStorage.getInstance().get(kingdomID);
        if (kingdomData == null) {
            return;
        }
        TanChatUtils.message(player, Lang.KINGDOM_CREATED_NEWSLETTER.get(player, tanPlayer.getNameStored(), kingdomData.getBaseColoredName()));
    }

    @Override
    public void broadcastConcerned(Player player) {
        broadcast(player);
    }

    @Override
    public GuiItem createGuiItem(Player player, LangType lang, Consumer<Player> onClick) {

        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(playerID);
        if (tanPlayer == null) {
            return null;
        }
        KingdomData kingdomData = KingdomDataStorage.getInstance().get(kingdomID);
        if (kingdomData == null) {
            return null;
        }

        return IconManager.getInstance().get(IconKey.BROWSE_KINGDOM_ICON)
                .setName(Lang.KINGDOM_CREATED_NEWSLETTER_TITLE.get(lang))
                .setDescription(
                        Lang.NEWSLETTER_DATE.get(DateUtil.getRelativeTimeDescription(lang, getDate())),
                        Lang.KINGDOM_CREATED_NEWSLETTER.get(tanPlayer.getNameStored(), kingdomData.getBaseColoredName()),
                        Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ.get()
                )
                .setAction(action -> {
                    action.setCancelled(true);
                    if (action.isRightClick()) {
                        markAsRead(player);
                        onClick.accept(player);
                    }
                })
                .asGuiItem(player, lang);
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
