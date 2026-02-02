package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.interfaces.TanTerritory;

import java.util.UUID;
import java.util.function.Consumer;



public class TerritoryVassalAcceptedNews extends Newsletter {

    private final String regionID;
    private final String townID;

    public TerritoryVassalAcceptedNews(TanTerritory regionID, TanTerritory townID) {
        super();
        this.regionID = regionID.getID();
        this.townID = townID.getID();
    }

    public TerritoryVassalAcceptedNews(UUID id, long date, String proposingTerritoryID, String receivingTerritoryID) {
        super(id, date);
        this.regionID = proposingTerritoryID;
        this.townID = receivingTerritoryID;
    }

    @Override
    public GuiItem createGuiItem(Player player, LangType lang, Consumer<Player> onClick) {
        TerritoryData region = TerritoryUtil.getTerritory(regionID);
        if (region == null)
            return null;
        TerritoryData town = TerritoryUtil.getTerritory(townID);
        if (town == null)
            return null;

       return IconManager.getInstance().get(IconKey.TOWN_BASE_ICON)
                .setName(Lang.TOWN_JOIN_REGION_ACCEPTED_NEWSLETTER_TITLE.get(lang))
                .setDescription(
                        Lang.TOWN_JOIN_REGION_ACCEPTED_NEWSLETTER.get(town.getCustomColoredName().toLegacyText(), region.getCustomColoredName().toLegacyText()),
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
        TerritoryData proposingTerritory = TerritoryUtil.getTerritory(regionID);
        if (proposingTerritory == null)
            return false;
        TerritoryData receivingTerritory = TerritoryUtil.getTerritory(townID);
        if (receivingTerritory == null)
            return false;
        return proposingTerritory.isPlayerIn(player) || receivingTerritory.isPlayerIn(player);
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.TERRITORY_VASSAL_ACCEPTED;
    }

    @Override
    public void broadcast(Player player, ITanPlayer tanPlayer) {
        TerritoryData proposingTerritory = TerritoryUtil.getTerritory(regionID);
        if (proposingTerritory == null)
            return;
        TerritoryData receivingTerritory = TerritoryUtil.getTerritory(townID);
        if (receivingTerritory == null)
            return;

        TanChatUtils.message(player, Lang.TOWN_JOIN_REGION_ACCEPTED_NEWSLETTER.get(
                tanPlayer,
                proposingTerritory.getCustomColoredName().toLegacyText(),
                receivingTerritory.getCustomColoredName().toLegacyText()),
                SoundEnum.MINOR_GOOD);
    }

    public String getProposingTerritoryID() {
        return regionID;
    }

    public String getReceivingTerritoryID() {
        return townID;
    }
}
