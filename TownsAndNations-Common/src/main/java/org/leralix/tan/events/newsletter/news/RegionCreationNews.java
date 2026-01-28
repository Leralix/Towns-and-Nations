package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.RegionData;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.utils.text.DateUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.TanRegion;

import java.util.UUID;
import java.util.function.Consumer;


public class RegionCreationNews extends Newsletter {

    private final String playerID;
    private final String regionID;

    public RegionCreationNews(TanRegion regionData, TanPlayer player) {
        super();
        playerID = player.getUUID().toString();
        regionID = regionData.getID();
    }

    public RegionCreationNews(UUID id, long date, String playerID, String regionID) {
        super(id, date);
        this.playerID = playerID;
        this.regionID = regionID;
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.REGION_CREATED;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getRegionID() {
        return regionID;
    }

    @Override
    public void broadcast(Player player) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getOrNull(playerID);
        if (tanPlayer == null)
            return;
        RegionData regionData = RegionDataStorage.getInstance().get(regionID);
        if (regionData == null)
            return;
        TanChatUtils.message(player, Lang.REGION_CREATED_NEWSLETTER.get(player, tanPlayer.getNameStored(), regionData.getColoredName()));
    }

    @Override
    public void broadcastConcerned(Player player) {
        broadcast(player);
    }

    @Override
    public GuiItem createGuiItem(Player player, LangType lang, Consumer<Player> onClick) {

        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getOrNull(playerID);
        if (tanPlayer == null)
            return null;
        RegionData regionData = RegionDataStorage.getInstance().get(regionID);
        if (regionData == null)
            return null;

        return IconManager.getInstance().get(IconKey.REGION_BASE_ICON)
                .setName(Lang.REGION_CREATED_NEWSLETTER_TITLE.get(lang))
                .setDescription(
                        Lang.NEWSLETTER_DATE.get(DateUtil.getRelativeTimeDescription(lang, getDate())),
                        Lang.REGION_CREATED_NEWSLETTER.get(tanPlayer.getNameStored(), regionData.getColoredName()),
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
