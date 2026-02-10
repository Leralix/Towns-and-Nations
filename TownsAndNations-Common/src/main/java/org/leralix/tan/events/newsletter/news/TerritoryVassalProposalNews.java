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
import org.leralix.tan.gui.user.territory.hierarchy.TerritoryChooseOverlordMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.DateUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.interfaces.territory.TanTerritory;

import java.util.UUID;
import java.util.function.Consumer;


public class TerritoryVassalProposalNews extends Newsletter {
    String proposingTerritoryID;
    String receivingTerritoryID;

    public TerritoryVassalProposalNews(TanTerritory proposingTerritory, TanTerritory receivingTerritory) {
        this(proposingTerritory.getID(), receivingTerritory.getID());
    }

    public TerritoryVassalProposalNews(String proposingTerritoryID, String receivingTerritoryID) {
        super();
        this.proposingTerritoryID = proposingTerritoryID;
        this.receivingTerritoryID = receivingTerritoryID;
    }

    public TerritoryVassalProposalNews(UUID id, long date, String proposingTerritoryID, String receivingTerritoryID) {
        super(id, date);
        this.proposingTerritoryID = proposingTerritoryID;
        this.receivingTerritoryID = receivingTerritoryID;
    }

    @Override
    public GuiItem createGuiItem(Player player, LangType lang, Consumer<Player> onClick) {
        TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
        TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
        if (proposingTerritory == null || receivingTerritory == null)
            return null;

        return IconManager.getInstance().get(Material.GOLDEN_HELMET)
                .setName(Lang.NEWSLETTER_JOIN_REGION_PROPOSAL.get(lang))
                .setDescription(
                        Lang.NEWSLETTER_DATE.get(DateUtil.getRelativeTimeDescription(lang, getDate())),
                        Lang.NEWSLETTER_JOIN_REGION_PROPOSAL_DESC1.get(proposingTerritory.getColoredName(), receivingTerritory.getColoredName())
                )
                .setClickToAcceptMessage(Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ)
                .setAction(event -> {
                    event.setCancelled(true);
                    if (event.isRightClick()) {
                        markAsRead(player);
                        onClick.accept(player);
                    }
                })
                .asGuiItem(player, lang);
    }

    @Override
    public GuiItem createConcernedGuiItem(Player player, LangType lang, Consumer<Player> onClick) {
        TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
        TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
        if (proposingTerritory == null || receivingTerritory == null)
            return null;

        return IconManager.getInstance().get(Material.GOLDEN_HELMET)
                .setName(Lang.NEWSLETTER_JOIN_REGION_PROPOSAL.get(lang))
                .setDescription(
                        Lang.NEWSLETTER_JOIN_REGION_PROPOSAL_DESC1.get(proposingTerritory.getColoredName(), receivingTerritory.getColoredName()),
                        Lang.NEWSLETTER_JOIN_REGION_PROPOSAL_DESC2.get()
                )
                .setClickToAcceptMessage(Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ)
                .setAction(action -> {
                    action.setCancelled(true);
                    if (action.isLeftClick())
                        new TerritoryChooseOverlordMenu(player, receivingTerritory, Player::closeInventory);
                    if (action.isRightClick()) {
                        markAsRead(player);
                        onClick.accept(player);
                    }
                })
                .asGuiItem(player, lang);
    }

    @Override
    public boolean shouldShowToPlayer(Player player) {
        TerritoryData territoryData = TerritoryUtil.getTerritory(receivingTerritoryID);
        if (territoryData == null)
            return false;
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        if (!territoryData.isPlayerIn(tanPlayer))
            return false;
        return territoryData.doesPlayerHavePermission(tanPlayer, RolePermission.TOWN_ADMINISTRATOR);
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.TERRITORY_VASSAL_PROPOSAL;
    }

    @Override
    public void broadcast(Player player, ITanPlayer tanPlayer) {
        TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
        if (proposingTerritory == null)
            return;
        TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
        if (receivingTerritory == null)
            return;
        TanChatUtils.message(player, Lang.TOWN_JOIN_REGION_PROPOSAL_NEWSLETTER.get(tanPlayer, proposingTerritory.getColoredName(), receivingTerritory.getColoredName()), SoundEnum.MINOR_BAD);
    }

    public String getProposingTerritoryID() {
        return proposingTerritoryID;
    }

    public String getReceivingTerritoryID() {
        return receivingTerritoryID;
    }
}
