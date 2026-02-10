package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.territory.rank.RolePermission;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.DateUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.interfaces.territory.TanTerritory;

import java.util.UUID;
import java.util.function.Consumer;


public class TerritoryVassalForcedNews extends Newsletter {
    private final String proposingTerritoryID;
    private final String forcedTerritoryID;

    public TerritoryVassalForcedNews(TanTerritory proposingTerritory, TanTerritory receivingTerritory) {
        this(proposingTerritory.getID(), receivingTerritory.getID());
    }

    public TerritoryVassalForcedNews(String proposingTerritoryID, String forcedTerritoryID) {
        super();
        this.proposingTerritoryID = proposingTerritoryID;
        this.forcedTerritoryID = forcedTerritoryID;
    }

    public TerritoryVassalForcedNews(UUID id, long date, String proposingTerritoryID, String forcedTerritoryID) {
        super(id, date);
        this.proposingTerritoryID = proposingTerritoryID;
        this.forcedTerritoryID = forcedTerritoryID;
    }

    @Override
    public GuiItem createGuiItem(Player player, LangType lang, Consumer<Player> onClick) {
        TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
        TerritoryData forcedTerritory = TerritoryUtil.getTerritory(forcedTerritoryID);
        if (proposingTerritory == null || forcedTerritory == null)
            return null;

        return IconManager.getInstance().get(Material.GOLDEN_HELMET)
                .setName(Lang.FORCED_VASSALAGE_TITLE.get(lang))
                .setDescription(
                        Lang.NEWSLETTER_DATE.get(DateUtil.getRelativeTimeDescription(lang, getDate())),
                        Lang.FORCED_VASSALAGE.get(proposingTerritory.getColoredName(), forcedTerritory.getColoredName())
                )
                .setClickToAcceptMessage(Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ)
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
        TerritoryData territoryData = TerritoryUtil.getTerritory(forcedTerritoryID);
        if (territoryData == null)
            return false;
        TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
        if (proposingTerritory == null)
            return false;
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        if (!territoryData.isPlayerIn(tanPlayer))
            return false;
        return territoryData.doesPlayerHavePermission(tanPlayer, RolePermission.TOWN_ADMINISTRATOR) ||
                proposingTerritory.doesPlayerHavePermission(tanPlayer, RolePermission.TOWN_ADMINISTRATOR);
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.TERRITORY_VASSAL_FORCED;
    }

    @Override
    public void broadcast(Player player, ITanPlayer tanPlayer) {
        TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
        if (proposingTerritory == null)
            return;
        TerritoryData receivingTerritory = TerritoryUtil.getTerritory(forcedTerritoryID);
        if (receivingTerritory == null)
            return;
        TanChatUtils.message(player, Lang.FORCED_VASSALAGE.get(tanPlayer, receivingTerritory.getColoredName(), proposingTerritory.getColoredName()), SoundEnum.MINOR_BAD);
    }

    public String getProposingTerritoryID() {
        return proposingTerritoryID;
    }

    public String getForcedTerritoryID() {
        return forcedTerritoryID;
    }
}
