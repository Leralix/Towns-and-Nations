package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.territory.rank.RolePermission;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.user.territory.relation.OpenDiplomacyProposalsMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.enums.EDiplomacyState;
import org.tan.api.interfaces.territory.TanTerritory;

import java.util.UUID;
import java.util.function.Consumer;


public class DiplomacyProposalNews extends Newsletter {
    private final String proposingTerritoryID;
    private final String receivingTerritoryID;
    private final TownRelation wantedRelation;

    public DiplomacyProposalNews(UUID id, long date, String proposingTerritoryID, String receivingTerritoryID, TownRelation wantedRelation) {
        super(id, date);
        this.proposingTerritoryID = proposingTerritoryID;
        this.receivingTerritoryID = receivingTerritoryID;
        this.wantedRelation = wantedRelation;
    }

    public DiplomacyProposalNews(TanTerritory proposingTerritory, TanTerritory receivingTerritory, EDiplomacyState wantedRelation) {
        super();
        this.proposingTerritoryID = proposingTerritory.getID();
        this.receivingTerritoryID = receivingTerritory.getID();
        this.wantedRelation = TownRelation.fromAPI(wantedRelation);
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.DIPLOMACY_PROPOSAL;
    }

    public String getProposingTerritoryID() {
        return proposingTerritoryID;
    }

    public String getReceivingTerritoryID() {
        return receivingTerritoryID;
    }

    public TownRelation getWantedRelation() {
        return wantedRelation;
    }

    @Override
    public void broadcast(Player player, ITanPlayer tanPlayer) {
        TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
        if (proposingTerritory == null)
            return;
        TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
        if (receivingTerritory == null)
            return;
        TanChatUtils.message(player,
                Lang.DIPLOMACY_PROPOSAL_NEWSLETTER.get(
                        tanPlayer,
                        proposingTerritory.getCustomColoredName().toLegacyText(),
                        receivingTerritory.getCustomColoredName().toLegacyText(),
                        wantedRelation.getColoredName(tanPlayer.getLang())),
                SoundEnum.MINOR_GOOD);
    }

    @Override
    public GuiItem createGuiItem(Player player, LangType lang, Consumer<Player> onClick) {
        TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
        TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
        if (proposingTerritory == null || receivingTerritory == null)
            return null;

        return createBasicNewsletter(
                Material.PAPER,
                Lang.NEWSLETTER_DIPLOMACY_PROPOSAL.get(proposingTerritory.getColoredName()),
                Lang.NEWSLETTER_DIPLOMACY_PROPOSAL_DESC1.get(receivingTerritory.getColoredName(), wantedRelation.getColoredName(lang)),
                lang,
                onClick,
                player
        );
    }

    @Override
    public GuiItem createConcernedGuiItem(Player player, LangType lang, Consumer<Player> onClick) {
        TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
        TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
        if (proposingTerritory == null || receivingTerritory == null)
            return null;

        return IconManager.getInstance().get(Material.PAPER)
                .setName(Lang.NEWSLETTER_DIPLOMACY_PROPOSAL.get(lang, proposingTerritory.getColoredName()))
                .setDescription(
                        Lang.NEWSLETTER_DIPLOMACY_PROPOSAL_DESC1.get(receivingTerritory.getColoredName(), wantedRelation.getColoredName(lang)),
                        Lang.NEWSLETTER_DIPLOMACY_PROPOSAL_DESC2.get()
                )
                .setClickToAcceptMessage(Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ)
                .setAction(action -> {
                    action.setCancelled(true);
                    if (action.isLeftClick()) {
                        if (receivingTerritory.doesPlayerHavePermission(player, RolePermission.MANAGE_TOWN_RELATION)) {
                            TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(lang), SoundEnum.NOT_ALLOWED);
                            return;
                        }
                        new OpenDiplomacyProposalsMenu(player, receivingTerritory);
                    }
                    if (action.isRightClick()) {
                        markAsRead(player);
                        onClick.accept(player);
                    }
                })
                .asGuiItem(player, lang);
    }

    @Override
    public boolean shouldShowToPlayer(Player player) {
        TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
        if (receivingTerritory == null)
            return false;
        TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
        if (proposingTerritory == null)
            return false;

        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        return proposingTerritory.isPlayerIn(tanPlayer) || receivingTerritory.isPlayerIn(tanPlayer);
    }
}
