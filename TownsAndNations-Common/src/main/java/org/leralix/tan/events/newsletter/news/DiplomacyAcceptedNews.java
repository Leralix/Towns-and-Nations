package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.DateUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.enums.EDiplomacyState;
import org.tan.api.interfaces.territory.TanTerritory;

import java.util.UUID;
import java.util.function.Consumer;


public class DiplomacyAcceptedNews extends Newsletter {
    private final String proposingTerritoryID;
    private final String receivingTerritoryID;
    private final TownRelation wantedRelation;
    private final boolean isRelationWorse;

    public DiplomacyAcceptedNews(UUID id, long date, String proposingTerritoryID, String receivingTerritoryID, TownRelation wantedRelation, boolean isRelationWorse) {
        super(id, date);
        this.proposingTerritoryID = proposingTerritoryID;
        this.receivingTerritoryID = receivingTerritoryID;
        this.wantedRelation = wantedRelation;
        this.isRelationWorse = isRelationWorse;
    }

    public DiplomacyAcceptedNews(TanTerritory proposingTerritory, TanTerritory receivingTerritory, EDiplomacyState newRelation, boolean isRelationBetter) {
        super();
        this.proposingTerritoryID = proposingTerritory.getID();
        this.receivingTerritoryID = receivingTerritory.getID();
        this.wantedRelation = TownRelation.fromAPI(newRelation);
        this.isRelationWorse = !isRelationBetter;
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.DIPLOMACY_ACCEPTED;
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

    public boolean isRelationWorse() {
        return isRelationWorse;
    }

    @Override
    public void broadcast(Player player, ITanPlayer tanPlayer) {
        TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
        if (proposingTerritory == null)
            return;
        TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
        if (receivingTerritory == null)
            return;

        LangType lang = tanPlayer.getLang();

        if (isRelationWorse) {
            TanChatUtils.message(player,
                    Lang.BROADCAST_RELATION_WORSEN.get(
                            lang,
                            proposingTerritory.getCustomColoredName().toLegacyText(),
                            receivingTerritory.getCustomColoredName().toLegacyText(),
                            wantedRelation.getColoredName(lang)),
                    SoundEnum.BAD);
        } else {
            TanChatUtils.message(player,
                    Lang.BROADCAST_RELATION_IMPROVE.get(
                            lang,
                            proposingTerritory.getCustomColoredName().toLegacyText(),
                            receivingTerritory.getCustomColoredName().toLegacyText(),
                            wantedRelation.getColoredName(lang)),
                    SoundEnum.GOOD);
        }
    }

    @Override
    public GuiItem createGuiItem(Player player, LangType lang, Consumer<Player> onClick) {

        TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
        if (proposingTerritory == null)
            return null;
        TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
        if (receivingTerritory == null)
            return null;


        return IconManager.getInstance().get(IconKey.NEWSLETTER_DIPLOMACY_ACCEPTED_ICON)
                .setName(Lang.DIPLOMACY_ACCEPT_NEWSLETTER_TITLE.get(lang))
                .setDescription(
                        Lang.NEWSLETTER_DATE.get(DateUtil.getRelativeTimeDescription(lang, getDate())),
                        Lang.DIPLOMACY_ACCEPT_NEWSLETTER.get(proposingTerritory.getCustomColoredName().toLegacyText(), receivingTerritory.getCustomColoredName().toLegacyText(), wantedRelation.getColoredName(lang))
                )
                .setClickToAcceptMessage(Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ)
                .setAction(action -> {
                            action.setCancelled(true);
                            if (action.isRightClick()) {
                                markAsRead(player);
                                onClick.accept(player);
                            }
                        }
                )
                .asGuiItem(player, lang);
    }

    @Override
    public GuiItem createConcernedGuiItem(Player player, LangType lang, Consumer<Player> onClick) {
        return createGuiItem(player, lang, onClick);
    }

    @Override
    public boolean shouldShowToPlayer(Player player) {
        TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
        if (proposingTerritory == null)
            return false;
        TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
        if (receivingTerritory == null)
            return false;
        return receivingTerritory.isPlayerIn(player) || proposingTerritory.isPlayerIn(player);
    }
}
